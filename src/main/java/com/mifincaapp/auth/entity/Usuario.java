
package com.mifincaapp.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "usuario", nullable = false, unique = true)
    private String username;
    
    @Column(name = "contraseña", nullable = false)
    private String password;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email; // Agregar campo de email
   
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "usuario_rol", 
      joinColumns = @JoinColumn(name = "usuario_id"), 
      inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles;
    
    public Usuario(){}

    public Usuario(String nombre, String username, String password, String email) {
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public String getEmail() { 
        return email;
    }
    
    public Set<Rol> getRoles() {
        return roles;
    }
    
    

    public void setId(long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    
}
