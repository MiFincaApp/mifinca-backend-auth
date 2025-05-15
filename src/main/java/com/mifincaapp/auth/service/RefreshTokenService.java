package com.mifincaapp.auth.service;

import com.mifincaapp.auth.entity.RefreshToken;
import com.mifincaapp.auth.entity.Usuario;
import com.mifincaapp.auth.enums.TipoCliente;
import com.mifincaapp.auth.repository.RefreshTokenRepository;
import com.mifincaapp.auth.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${JWT_REFRESH_EXPIRATION_MS}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RefreshToken crearRefreshToken(String username, TipoCliente tipoCliente) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        //Verificar si ya tiene un token activo para este tipo de cliente
        Optional<RefreshToken> existente = refreshTokenRepository
                .findByUsuarioAndTipoCliente(usuario, tipoCliente);
        
        if (existente.isPresent()) {
            throw new RuntimeException("Ya tienes una sesión activa en este tipo de cliente: " + tipoCliente);
        }

        //Crear un nuevo refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setFechaCreacion(Instant.now());
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setTipoCliente(tipoCliente);
        
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> verificarExpiracion(RefreshToken token) {
        if (token.getFechaExpiracion().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("El refresh token ha expirado. Debes hacer login de nuevo.");
        }
        return Optional.of(token);
    }

    @Transactional
    public void eliminarPorUsuario(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }

    public Optional<RefreshToken> buscarPorRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }
    
    @Transactional
    public RefreshToken rotarRefreshToken(RefreshToken oldToken){
        //Eliminar el refresh token anterior
        refreshTokenRepository.delete(oldToken);
        
        //Crear uno nuevo para el mismo usuario
        RefreshToken nuevoToken = new RefreshToken();
        nuevoToken.setUsuario(oldToken.getUsuario()); //Setea tipo de cliente
        nuevoToken.setTipoCliente(oldToken.getTipoCliente());
        nuevoToken.setRefreshToken(UUID.randomUUID().toString());
        nuevoToken.setFechaCreacion(Instant.now()); // importante para consistencia
        nuevoToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));
        
        return refreshTokenRepository.save(nuevoToken);
        
    }
}
