package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Obrero {
    private Long id;
    private String especialidad;
    private Integer experiencia;
    private String certificaciones;
    private String descripcion;
    private Double tarifaHora;
    private Boolean disponibilidad;
    
    // Constructor para crear obrero básico
    public Obrero(Long id, String especialidad, Integer experiencia, Boolean disponibilidad) {
        this.id = id;
        this.especialidad = especialidad;
        this.experiencia = experiencia;
        this.disponibilidad = disponibilidad;
        this.certificaciones = "";
        this.descripcion = "";
        this.tarifaHora = null;
    }
}
