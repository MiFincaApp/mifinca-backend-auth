
package com.mifincaapp.auth.controller;


import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, String> bienvenida(){
        return Map.of(
            "mensaje", "¡Bienvenido a la API de MifincaApp!",
            "estado", "protegido",
            "detalle", "Debes autenticarte para acceder a los recursos."
        );
              
    }
}
