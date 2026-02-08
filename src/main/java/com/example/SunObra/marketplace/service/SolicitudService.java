package com.example.SunObra.marketplace.service;

import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.enums.Urgencia;
import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepo;

    public SolicitudService(SolicitudRepository solicitudRepo) {
        this.solicitudRepo = solicitudRepo;
    }

    public List<Solicitud> listarPorCliente(Long clienteId) {
        return solicitudRepo.findByClienteIdOrderByIdDesc(clienteId);
    }

    public Solicitud obtenerDetalle(Long solicitudId, Long clienteId) {
        return solicitudRepo.findByIdAndClienteId(solicitudId, clienteId).orElse(null);
    }

    @Transactional
    public Solicitud crear(Long clienteId, SolicitudCreateRequest req) {
        validar(req);

        Solicitud s = new Solicitud();
        s.setClienteId(clienteId);
        s.setEspecialidad(req.getEspecialidad().trim());
        s.setDescripcion(req.getDescripcion().trim());

        if (req.getUbicacion() != null && !req.getUbicacion().trim().isEmpty()) {
            s.setUbicacion(req.getUbicacion().trim());
        } else {
            s.setUbicacion(null);
        }

        s.setPresupuestoMin(req.getPresupuestoMin());
        s.setPresupuestoMax(req.getPresupuestoMax());

        // Si viene null, ponemos MEDIA
        s.setUrgencia(req.getUrgencia() != null ? req.getUrgencia() : Urgencia.MEDIA);

        // Siempre inicia ABIERTA
        s.setEstado(SolicitudStatus.ABIERTA);

        return solicitudRepo.save(s);
    }

    private void validar(SolicitudCreateRequest req) {
        if (req.getEspecialidad() == null || req.getEspecialidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        if (req.getDescripcion() == null || req.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        if (req.getPresupuestoMin() != null && req.getPresupuestoMax() != null) {
            if (req.getPresupuestoMin().compareTo(req.getPresupuestoMax()) > 0) {
                throw new IllegalArgumentException("El presupuesto mínimo no puede ser mayor al máximo.");
            }
        }

        // opcional: bloquear negativos
        if (req.getPresupuestoMin() != null && req.getPresupuestoMin().signum() < 0) {
            throw new IllegalArgumentException("El presupuesto mínimo no puede ser negativo.");
        }
        if (req.getPresupuestoMax() != null && req.getPresupuestoMax().signum() < 0) {
            throw new IllegalArgumentException("El presupuesto máximo no puede ser negativo.");
        }
    }
}
