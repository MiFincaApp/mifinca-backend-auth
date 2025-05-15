
package com.mifincaapp.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;

public class EnvTestConfig {
    @BeforeAll
    public static void cargarVariablesDeEntorno() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // raíz del proyecto
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
