package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Modelo para los proyectos mostrados en la página principal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private String location;
    
    // Constructor para crear proyectos de ejemplo
    public static Project[] getSampleProjects() {
        return new Project[]{
            new Project(1L, "Proyecto 1", "Remodelación de vivienda en Bogotá.", "Remodelación", "/images/gallary-1.jpg", "Bogotá"),
            new Project(2L, "Proyecto 2", "Construcción de local comercial.", "Construcción", "/images/gallary-2.jpg", "Bogotá"),
            new Project(3L, "Proyecto 3", "Obra nueva en conjunto residencial.", "Obra Nueva", "/images/gallary-3.jpg", "Bogotá"),
            new Project(4L, "Proyecto 4", "Reparación de fachada institucional.", "Reparación", "/images/main.jpg", "Bogotá")
        };
    }
}
