
package com.mifincaapp.auth.repository;

import com.mifincaapp.auth.entity.RefreshToken;
import com.mifincaapp.auth.entity.Usuario;
import com.mifincaapp.auth.enums.TipoCliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByUsuarioAndTipoCliente(Usuario usuario, TipoCliente tipoCliente);
    void deleteByUsuario(Usuario usuario);
}
