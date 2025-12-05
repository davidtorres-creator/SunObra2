package com.example.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para exponer datos de usuario sin campos sensibles (contraseña).
 */
public class UserDto {
    private Long id;

    @NotBlank
    @Size(min=2, max=80)
    private String nombre;

    @NotBlank
    @Size(min=2, max=80)
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String userType;  // "cliente", "obrero" o "admin"

    private String especialidades;
    private Integer experiencia;


    // getters y setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }

    public Integer getExperiencia() { return experiencia; }
    public void setExperiencia(Integer experiencia) { this.experiencia = experiencia; }
}
