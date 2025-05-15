package com.mifincaapp.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtil {

    //Clave secreta de minimo 32 caracteres para HS256
    private final Key key;

    private final long jwtExpirationMs;

    public JwtUtil(
            @Value("${JWT_SECRET}") String jwtSecret,
            @Value("${JWT_EXPIRATION_MS}") long jwtExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generarToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // Agrega los roles al token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> obtenerRoles(String token) {
        Claims claims = validarToken(token);
        return claims.get("roles", List.class); // Extrae los roles desde el claim "roles"
    }
}
