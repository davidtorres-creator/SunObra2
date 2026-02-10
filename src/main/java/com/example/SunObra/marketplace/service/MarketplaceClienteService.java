package com.example.SunObra.marketplace.service;


import com.example.SunObra.marketplace.enums.CotizacionStatus;
import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.enums.ServicioStatus;
import com.example.SunObra.marketplace.model.Cotizacion;
import com.example.SunObra.marketplace.model.Servicio;
import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.repository.CotizacionRepository;
import com.example.SunObra.marketplace.repository.ServicioRepository;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarketplaceClienteService {

    private final SolicitudRepository solicitudRepo;
    private final CotizacionRepository cotizacionRepo;
    private final ServicioRepository servicioRepo;

    public MarketplaceClienteService(SolicitudRepository solicitudRepo,
                                     CotizacionRepository cotizacionRepo,
                                     ServicioRepository servicioRepo) {
        this.solicitudRepo = solicitudRepo;
        this.cotizacionRepo = cotizacionRepo;
        this.servicioRepo = servicioRepo;
    }

    public List<Cotizacion> cotizacionesDeSolicitud(Long solicitudId) {
        return cotizacionRepo.findBySolicitudIdOrderByIdAsc(solicitudId);
    }

    @Transactional
    public void aceptarCotizacion(Long clienteId, Long solicitudId, Long cotizacionId) {

        // 1) Validar solicitud existe y es del cliente
        Solicitud solicitud = solicitudRepo.findByIdAndClienteId(solicitudId, clienteId).orElse(null);
        if (solicitud == null) {
            throw new IllegalArgumentException("No tienes acceso a esta solicitud (o no existe).");
        }

        // 2) Debe estar ABIERTA
        if (solicitud.getEstado() != SolicitudStatus.ABIERTA) {
            throw new IllegalArgumentException("La solicitud ya no está ABIERTA.");
        }

        // 3) Evitar crear 2 servicios por la misma solicitud
        if (servicioRepo.findBySolicitudId(solicitudId).isPresent()) {
            throw new IllegalArgumentException("Ya existe un servicio para esta solicitud.");
        }

        // 4) Validar cotización existe y pertenece a la solicitud
        Cotizacion elegida = cotizacionRepo.findById(cotizacionId).orElse(null);
        if (elegida == null || !elegida.getSolicitudId().equals(solicitudId)) {
            throw new IllegalArgumentException("La cotización no pertenece a esta solicitud.");
        }

        // 5) Crear servicio
        Servicio servicio = new Servicio();
        servicio.setSolicitudId(solicitudId);
        servicio.setClienteId(clienteId);
        servicio.setObreroId(elegida.getObreroId());
        servicio.setPrecioAcordado(elegida.getPrecio());
        servicio.setEstado(ServicioStatus.PROGRAMADO);

        servicioRepo.save(servicio);

        // 6) Cambiar estados de cotizaciones: elegida ACEPTADA, demás RECHAZADA
        List<Cotizacion> todas = cotizacionRepo.findBySolicitudId(solicitudId);
        for (Cotizacion c : todas) {
            if (c.getId().equals(cotizacionId)) {
                c.setEstado(CotizacionStatus.ACEPTADA);
            } else {
                c.setEstado(CotizacionStatus.RECHAZADA);
            }
        }
        cotizacionRepo.saveAll(todas);

        // 7) Cerrar solicitud
        solicitud.setEstado(SolicitudStatus.CERRADA);
        solicitudRepo.save(solicitud);
    }
}
