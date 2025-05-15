
package com.mifincaapp.auth.dto;

import com.mifincaapp.auth.enums.TipoCliente;
import jakarta.validation.constraints.NotBlank;


public class LoginRequest {
    
   @NotBlank(message = "El username es obligatorio")
   private String username;
   
   @NotBlank(message = "La contraseña es obligatoria")
   private String password;
   
   @NotBlank(message = "El tipo de cliente es obligatorio")
   private TipoCliente tipoCliente;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }
    

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
    
}
