package com.example.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para representar un usuario del sistema
 * Equivalente a los datos de usuario del PHP
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String rol;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 500)
    private String direccion;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor adicional para compatibilidad
    public User(Long id, String nombre, String apellido, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.telefono = "";
        this.direccion = "";
        this.activo = true;
        this.password = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor para crear usuario con contraseña
    public User(String nombre, String apellido, String email, String password, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.telefono = "";
        this.direccion = "";
        this.activo = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructor para crear un usuario de ejemplo
    public static User getSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Usuario de Prueba");
        user.setEmail("usuario@sunobra.com");
        user.setRol("cliente");
        user.setTelefono("3138385779");
        user.setDireccion("Bogotá, Colombia");
        user.setActivo(true);
        user.setPassword("cliente");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    // Constructor para crear un usuario admin de ejemplo
    public static User getSampleAdminUser() {
        User user = new User();
        user.setId(1L);
        user.setNombre("Administrador");
        user.setEmail("admin@sunobra.com");
        user.setRol("admin");
        user.setTelefono("3138385779");
        user.setDireccion("Bogotá, Colombia");
        user.setActivo(true);
        user.setPassword("admin");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    // Constructor para crear un usuario obrero de ejemplo
    public static User getSampleObreroUser() {
        User user = new User();
        user.setId(2L);
        user.setNombre("Obrero de Prueba");
        user.setEmail("obrero@sunobra.com");
        user.setRol("obrero");
        user.setTelefono("3138385779");
        user.setDireccion("Bogotá, Colombia");
        user.setActivo(true);
        user.setPassword("obrero");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    // Método para verificar si el usuario es admin
    public boolean isAdmin() {
        return "admin".equals(this.rol);
    }
    
    // Método para verificar si el usuario es cliente
    public boolean isCliente() {
        return "cliente".equals(this.rol);
    }
    
    // Método para verificar si el usuario es obrero
    public boolean isObrero() {
        return "obrero".equals(this.rol);
    }
}
