
package com.mifincaapp.auth.config;

//import static org.springframework.boot.webservices.client.WebServiceMessageSenderFactory.http;
import com.mifincaapp.auth.service.CustomUserDetailsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Habilita @PreAuthorize
public class SecurityConfig {
    
    //Cargan los valores desde el .env
    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;

    @Value("${ALLOWED_METHODS}")
    private String allowedMethods;

    @Value("${CUSTOM_HEADER_NAME}")
    private String customHeaderName;
      
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception{
        http
                 // Protección CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF (útil para pruebas con Postman)
                .authorizeHttpRequests(auth -> auth
                        
                        //Rutas Publicas
                        .requestMatchers("/", "/usuarios/login", "/usuarios/registro").permitAll() //permite el login y registro sin autenticación
                        
                         // Acceso según roles
                        .requestMatchers("/usuarios/perfil").authenticated()  // Solo acceso autenticado
                        .requestMatchers("/usuarios").hasRole("ADMIN") // Solo ADMIN puede ver los usuarios
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/campesino/**").hasRole("CAMPESINO")
                        .requestMatchers("/comprador/**").hasRole("COMPRADOR")
                        
                        //cualquier otra ruta requerira autenticacion
                        .anyRequest().authenticated() 
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable());
                
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of(allowedMethods.split(",")));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", customHeaderName));
        config.setAllowCredentials(true); // importante al usar cookies o headers personalizados

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);// Configuración de CORS para todas las rutas
        return source;
    }
   
     @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
