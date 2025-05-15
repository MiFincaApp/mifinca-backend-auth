
package com.mifincaapp.auth.service;

import com.mifincaapp.auth.dto.RegistroRequest;
import com.mifincaapp.auth.entity.Rol;
import com.mifincaapp.auth.entity.Usuario;
import com.mifincaapp.auth.enums.RolEnum;
import com.mifincaapp.auth.exception.UsuarioException;
import com.mifincaapp.auth.factory.UsuarioFactory;
import com.mifincaapp.auth.repository.RolRepository;
import com.mifincaapp.auth.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    public Usuario registrarUsuarioDesdeRequest(RegistroRequest request) {
        String username = request.getUsername().toLowerCase().trim();
        String email = request.getEmail().toLowerCase().trim();
        String tipoUsuario = request.getTipoUsuario();

        // Validación adicional
        if (tipoUsuario == null || tipoUsuario.isEmpty()) {
            throw new UsuarioException("El tipo de usuario es obligatorio.");
        }
        
        String tipoUsuarioUpper = tipoUsuario.toUpperCase().trim(); 

        if (usuarioRepository.existsByUsername(username)) {
            throw new UsuarioException("El nombre de usuario ya está registrado.");
        }

        if (tipoUsuario.equals("ADMIN")) {
            throw new UsuarioException("No está permitido registrar usuarios con rol ADMIN.");
        }

        RolEnum rolEnum;
        try {
            rolEnum = RolEnum.valueOf(tipoUsuario);
        } catch (IllegalArgumentException e) {
            throw new UsuarioException("Rol inválido. Solo se permite CAMPESINO o COMPRADOR.");
        }

        Rol rol = rolRepository.findByNombreRol(rolEnum.name())
                .orElseThrow(() -> new UsuarioException("Rol no encontrado: " + tipoUsuario));

        Usuario usuario = UsuarioFactory.crearDesdeRegistro(request, rol, passwordEncoder);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /*
    public void encriptarContrasenasNoCifradas(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        for(Usuario usuario : usuarios){
            String contrasena = usuario.getPassword();
            
            //Si la contrasena no esta cifrada (no empieza por "$2a$")
            if(!contrasena.startsWith("$2a$")){
                String nuevaContrasenaCifrada = passwordEncoder.encode(contrasena);
                usuario.setPassword(nuevaContrasenaCifrada);
                usuarioRepository.save(usuario);
                System.out.println("Contraseña actualizada o cifrada para el usuario: " + usuario.getNombre());
                
            }
        }
    }
     */
    public boolean login(String username, String password) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByUsername(username);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            return passwordEncoder.matches(password, usuario.getPassword());
        }
        return false;
    }

}
