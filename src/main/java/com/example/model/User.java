package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Modelo para representar un usuario del sistema
 * Equivalente a los datos de usuario del PHP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private String telefono;
    private String direccion;
    private boolean activo;
    
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
