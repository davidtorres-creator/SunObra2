package com.example.service;


import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.enums.ServicioStatus;
import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.model.Servicio;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import com.example.SunObra.marketplace.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que genera alertas simples para el panel de administración.
 *
 * Detecta:
 *  - Solicitudes abiertas (no cerradas ni canceladas) por más de 30 días.
 *  - Servicios en proceso por más de 60 días.
 */
@Service
public class AdminAlertService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    /**
     * Obtiene las alertas actuales.
     * Devuelve un mapa con listas de entidades bajo las claves "solicitudes" y "servicios".
     */
    public Map<String, List<?>> getAlerts() {
        Map<String, List<?>> alerts = new HashMap<>();

        // Solicitudes pendientes (más de 30 días)
        List<Solicitud> solAlert = solicitudRepository.findAll().stream()
                .filter(s -> s.getEstado() != null &&
                        s.getCreatedAt() != null &&
                        !EnumSet.of(SolicitudStatus.CERRADA, SolicitudStatus.CANCELADA).contains(s.getEstado()) &&
                        s.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.toList());
        alerts.put("solicitudes", solAlert);

        // Servicios en proceso (más de 60 días)
        List<Servicio> servAlert = servicioRepository.findAll().stream()
                .filter(s -> s.getEstado() != null &&
                        s.getCreatedAt() != null &&
                        s.getEstado() == ServicioStatus.EN_PROCESO &&
                        s.getCreatedAt().isBefore(LocalDateTime.now().minusDays(60)))
                .collect(Collectors.toList());
        alerts.put("servicios", servAlert);

        return alerts;
    }
}
