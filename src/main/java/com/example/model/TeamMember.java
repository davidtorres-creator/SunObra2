package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Modelo para los miembros del equipo mostrados en la sección de redes sociales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
    private Long id;
    private String name;
    private String position;
    private String description;
    private String imageUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String linkedinUrl;
    
    // Constructor para crear miembros del equipo de ejemplo
    public static TeamMember[] getSampleTeamMembers() {
        return new TeamMember[]{
            new TeamMember(1L, "David Torres", "Fundador y CEO", 
                "Apasionado por la construcción y la tecnología.", 
                "https://via.placeholder.com/300x300/667eea/ffffff?text=David+Torres",
                "#", "#", null, null),
            new TeamMember(2L, "Felipe Bermudez", "Co-fundador", 
                "Experto en gestión de proyectos y redes sociales.", 
                "https://via.placeholder.com/300x300/667eea/ffffff?text=Felipe+Bermudez",
                null, null, "#", "#"),
            new TeamMember(3L, "Dilan Ruiz", "Líder de operaciones", 
                "Conectando talento con oportunidades.", 
                "https://via.placeholder.com/300x300/667eea/ffffff?text=Dilan+Ruiz",
                "#", "#", null, null)
        };
    }
}
