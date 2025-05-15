package com.mifincaapp.auth.dto;

import java.util.List;

public class UsuarioResponse {
    private String nombre;
    private String username;
    private String email;
    private List<String> roles;

    public UsuarioResponse(String nombre, String username, String email, List<String> roles) {
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
