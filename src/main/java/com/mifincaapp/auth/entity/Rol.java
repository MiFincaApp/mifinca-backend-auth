
package com.mifincaapp.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombreRol;

    public Rol() {
    }
    

    public Long getId() {
        return id;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreRol(String nombre) {
        this.nombreRol = nombre;
    }
    
    
    
    
}
