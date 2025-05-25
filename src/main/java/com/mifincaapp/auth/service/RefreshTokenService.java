package com.mifincaapp.auth.service;

import com.mifincaapp.auth.entity.RefreshToken;
import com.mifincaapp.auth.entity.Usuario;
import com.mifincaapp.auth.enums.TipoCliente;
import com.mifincaapp.auth.repository.RefreshTokenRepository;
import com.mifincaapp.auth.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));

        //Crear un nuevo refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setFechaCreacion(now);
        refreshToken.setFechaExpiracion(now.plusSeconds(refreshTokenDurationMs / 1000));
        refreshToken.setTipoCliente(tipoCliente);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> verificarExpiracion(RefreshToken token) {

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("America/Bogota"));

        // Convertir fechaExpiracion (LocalDateTime) a OffsetDateTime con zona Bogotá
        OffsetDateTime expiracion = token.getFechaExpiracion().atZone(ZoneId.of("America/Bogota")).toOffsetDateTime();

        if (expiracion.isBefore(now)) {
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
    public RefreshToken rotarRefreshToken(RefreshToken oldToken) {
        //Eliminar el refresh token anterior
        refreshTokenRepository.delete(oldToken);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Bogota"));

        //Crear uno nuevo para el mismo usuario
        RefreshToken nuevoToken = new RefreshToken();
        nuevoToken.setUsuario(oldToken.getUsuario()); //Setea tipo de cliente
        nuevoToken.setTipoCliente(oldToken.getTipoCliente());
        nuevoToken.setRefreshToken(UUID.randomUUID().toString());
        nuevoToken.setFechaCreacion(now); // importante para consistencia
        nuevoToken.setFechaExpiracion(now.plusSeconds(refreshTokenDurationMs / 1000));

        return refreshTokenRepository.save(nuevoToken);

    }
}
