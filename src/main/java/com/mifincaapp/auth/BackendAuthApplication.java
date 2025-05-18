package com.mifincaapp.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendAuthApplication{

    public static void main(String[] args) {
        //Detecta el entorno, desarrollo o producción
        String envType = System.getenv("ENV_TYPE");
        if("local".equalsIgnoreCase(envType)){
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
        }else {
            System.out.println("No cargando .env, asumiendo entorno producción");
        }

        SpringApplication.run(BackendAuthApplication.class, args);
        
    }

}
