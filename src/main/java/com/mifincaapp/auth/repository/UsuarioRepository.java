
package com.mifincaapp.auth.repository;

import com.mifincaapp.auth.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);  // Este método busca el email
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
