package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Modelo para la configuración del sistema
 * Equivalente a la tabla 'settings' del PHP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    private String logoUrl;
    private String siteName;
    private String siteDescription;
    private String contactPhone;
    private String contactEmail;
    private String contactAddress;
    
    // Constructor para crear settings por defecto
    public static Settings getDefaultSettings() {
        Settings settings = new Settings();
        settings.setLogoUrl("/images/logo.png");
        settings.setSiteName("SunObra");
        settings.setSiteDescription("Plataforma de Servicios de Construcción");
        settings.setContactPhone("3138385779");
        settings.setContactEmail("sunobra69@gmail.com");
        settings.setContactAddress("Bogotá, Colombia");
        return settings;
    }
}
