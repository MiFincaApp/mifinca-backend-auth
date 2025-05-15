package com.mifincaapp.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendAuthApplication{

    public static void main(String[] args) {
        
        Dotenv dotenv = Dotenv.configure().load();
        // Establece las variables como propiedades del sistema
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());  // Configura las variables como propiedades del sistema
            System.out.println(entry.getKey() + "=" + entry.getValue());  // Muestra las variables cargadas
        });

        SpringApplication.run(BackendAuthApplication.class, args);
        
    }

}
