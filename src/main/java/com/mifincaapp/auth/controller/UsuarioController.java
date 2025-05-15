package com.mifincaapp.auth.controller;

import com.mifincaapp.auth.dto.LoginRequest;
import com.mifincaapp.auth.dto.RegistroRequest;
import com.mifincaapp.auth.dto.TokenRefreshRequest;
import com.mifincaapp.auth.dto.TokenRefreshResponse;
import com.mifincaapp.auth.dto.UsuarioResponse;
import com.mifincaapp.auth.entity.RefreshToken;
import com.mifincaapp.auth.entity.Usuario;
import com.mifincaapp.auth.repository.UsuarioRepository;
import com.mifincaapp.auth.service.RefreshTokenService;
import com.mifincaapp.auth.service.UsuarioService;
import com.mifincaapp.auth.util.JwtUtil;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //se quita @Controller ya que solo se usa si devolvemos vistas HTML como thymeleaf pero no es el caso
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        // Validación: Verifica si el nombre de usuario ya está registrado
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        // Validación: Verifica si el correo electrónico ya está registrado
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }

        try {
            // Si pasa la validación, procedemos a registrar el usuario
            Usuario nuevoUsuario = usuarioService.registrarUsuarioDesdeRequest(request);
            return ResponseEntity.ok("Usuario registrado exitosamente con username: " + nuevoUsuario.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error en el registro: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        boolean autenticado = usuarioService.login(request.getUsername(), request.getPassword());

        if (autenticado) {
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).get();

            //Roles para el JWT
            List<String> roles = usuario.getRoles().stream()
                    .map(rol -> rol.getNombreRol())
                    .collect(Collectors.toList());

            //Crear nuevo access token y refresh token
            String token = jwtUtil.generarToken(request.getUsername(), roles);
            RefreshToken refreshToken = refreshTokenService.crearRefreshToken(request.getUsername(), request.getTipoCliente());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("refreshToken", refreshToken.getRefreshToken());

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "CREDENCIALES INCORRECTAS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Acceso concedido para ADMIN");
    }

    @GetMapping("/campesino")
    @PreAuthorize("hasRole('CAMPESINO')")
    public ResponseEntity<String> campesinoAccess() {
        return ResponseEntity.ok("Acceso concedido para CAMPESINO");
    }

    @GetMapping("/comprador")
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<String> compradorAccess() {
        return ResponseEntity.ok("Acceso concedido para COMPRADOR");
    }

    @GetMapping("/perfil")
    public ResponseEntity<Usuario> verPerfil(Principal principal) {
        String username = principal.getName(); // Obtienes el nombre del usuario autenticado
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(usuario); // Devuelve la información del usuario autenticado
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> editarPerfil(@RequestBody Usuario usuarioEditado, Principal principal) {
        // Verifica que el nombre de usuario del perfil coincida con el usuario autenticado
        if (!usuarioEditado.getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Aquí actualiza los datos del usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar los campos necesarios
        usuario.setNombre(usuarioEditado.getNombre());
        usuario.setEmail(usuarioEditado.getEmail());

        usuarioRepository.save(usuario);  // Guardas el usuario actualizado

        // Convertir la entidad Usuario a UsuarioResponse para la respuesta
        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombreRol())
                .collect(Collectors.toList());

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getNombre(),
                usuario.getUsername(),
                usuario.getEmail(),
                roles
        );

        // Devolver el usuario actualizado en la respuesta
        return ResponseEntity.ok(usuarioResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refrescarToken(@RequestBody TokenRefreshRequest request) {
        String requestToken = request.getRefreshToken();

        return refreshTokenService.buscarPorRefreshToken(requestToken)
                .flatMap(refreshTokenService::verificarExpiracion)
                .map(refreshToken -> {
                    String username = refreshToken.getUsuario().getUsername();
                    List<String> roles = refreshToken.getUsuario().getRoles().stream()
                            .map(r -> r.getNombreRol())
                            .collect(Collectors.toList());

                    String nuevoAccessToken = jwtUtil.generarToken(username, roles);
                    RefreshToken nuevoRefreshToken = refreshTokenService.rotarRefreshToken(refreshToken);

                    return ResponseEntity.ok(
                            new TokenRefreshResponse(nuevoAccessToken, nuevoRefreshToken.getRefreshToken()
                            ));
                }).orElseThrow(() -> new RuntimeException("Refresh token no válido"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        refreshTokenService.eliminarPorUsuario(usuario);
        return ResponseEntity.ok("Sesion cerrada y refresh token revocado");
    }

}
