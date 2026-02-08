package com.example.SunObra.marketplace.service;


import com.example.SunObra.marketplace.enums.CotizacionStatus;
import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.model.Cotizacion;
import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.repository.CotizacionRepository;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CotizacionService {

    private final CotizacionRepository cotRepo;
    private final SolicitudRepository solRepo;

    public CotizacionService(CotizacionRepository cotRepo, SolicitudRepository solRepo) {
        this.cotRepo = cotRepo;
        this.solRepo = solRepo;
    }

    public Cotizacion buscarCotizacion(Long solicitudId, Long obreroId) {
        return cotRepo.findBySolicitudIdAndObreroId(solicitudId, obreroId).orElse(null);
    }

    public Solicitud obtenerSolicitud(Long solicitudId) {
        return solRepo.findById(solicitudId).orElse(null);
    }

    @Transactional
    public Cotizacion crearCotizacion(Long solicitudId, Long obreroId, CotizacionCreateRequest req) {
        validar(req);

        Solicitud solicitud = solRepo.findById(solicitudId).orElse(null);
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no existe.");
        }
        if (solicitud.getEstado() != SolicitudStatus.ABIERTA) {
            throw new IllegalArgumentException("Esta solicitud ya no está abierta para cotizar.");
        }

        // Evitar doble cotización (además de la restricción UNIQUE en BD)
        Cotizacion existente = buscarCotizacion(solicitudId, obreroId);
        if (existente != null) {
            throw new IllegalArgumentException("Ya enviaste una cotización para esta solicitud.");
        }

        Cotizacion c = new Cotizacion();
        c.setSolicitudId(solicitudId);
        c.setObreroId(obreroId);
        c.setPrecio(req.getPrecio());
        c.setMensaje(req.getMensaje() != null ? req.getMensaje().trim() : null);
        c.setTiempoEstimadoDias(req.getTiempoEstimadoDias());
        c.setEstado(CotizacionStatus.ENVIADA);

        return cotRepo.save(c);
    }

    private void validar(CotizacionCreateRequest req) {
        if (req.getPrecio() == null) {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }
        if (req.getPrecio().signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (req.getTiempoEstimadoDias() != null && req.getTiempoEstimadoDias() < 0) {
            throw new IllegalArgumentException("El tiempo estimado no puede ser negativo.");
        }
    }
}

