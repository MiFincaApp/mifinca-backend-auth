package com.mifincaapp.auth.factory;


import com.mifincaapp.auth.dto.RegistroRequest;
import com.mifincaapp.auth.entity.Rol;
import com.mifincaapp.auth.entity.Usuario;
import java.util.Collections;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsuarioFactory {

    public static Usuario crearDesdeRegistro(RegistroRequest request, Rol rol, PasswordEncoder encoder) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setUsername(request.getUsername().toLowerCase().trim());
        usuario.setEmail(request.getEmail().toLowerCase().trim());
        usuario.setPassword(encoder.encode(request.getPassword()));
        usuario.setRoles(Collections.singleton(rol));
        return usuario;
    }

}
