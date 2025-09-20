package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    private String userType; // "cliente", "obrero", "admin"

    private String telefono;
    private String direccion;
    
    @Column(name = "preferencias_contacto")
    private String preferenciasContacto;

    // Campos específicos para obreros
    private String especialidades; // JSON o string separado por comas
    private Integer experiencia;
    
    @Column(name = "tarifa_hora")
    private Double tarifaHora;
    
    private String certificaciones;
    private String descripcion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "activo")
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    // Constructor específico para login
    public usuarios(Long id, String nombre, String apellido, String email, String userType) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.userType = userType;
    }

    public static usuarios getSampleAdminUser() {
        usuarios admin = new usuarios();
        admin.setId(1L);
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setEmail("admin@sunobra.com");
        admin.setUserType("admin");
        admin.setTelefono("3138385779");
        admin.setDireccion("Bogotá, Colombia");
        admin.setPreferenciasContacto("Email");
        admin.setActivo(true);
        admin.setFechaRegistro(LocalDateTime.now());
        return admin;
    }
}