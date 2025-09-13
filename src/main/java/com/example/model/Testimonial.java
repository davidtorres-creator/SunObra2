package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Modelo para los testimonios mostrados en la página principal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonial {
    private Long id;
    private String name;
    private String role;
    private String content;
    
    // Constructor para crear testimonios de ejemplo
    public static Testimonial[] getSampleTestimonials() {
        return new Testimonial[]{
            new Testimonial(1L, "Felipe Bermudez", "Propietario", 
                "SunObra me ayudó a encontrar profesionales confiables y rápidos para mi proyecto. ¡Excelente plataforma!"),
            new Testimonial(2L, "David Torres", "Propietario", 
                "La gestión y seguimiento de mi obra fue muy fácil gracias a SunObra. 100% recomendado."),
            new Testimonial(3L, "Dilan Ruiz", "Propietario", 
                "Encontré trabajo rápidamente y pude mostrar mis habilidades a nuevos clientes.")
        };
    }
}
