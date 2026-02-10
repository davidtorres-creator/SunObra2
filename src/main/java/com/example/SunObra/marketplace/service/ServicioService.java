package com.example.SunObra.marketplace.service;

import com.example.SunObra.marketplace.enums.ServicioStatus;
import com.example.SunObra.marketplace.model.Servicio;
import com.example.SunObra.marketplace.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepo;

    public ServicioService(ServicioRepository servicioRepo) {
        this.servicioRepo = servicioRepo;
    }

    public List<Servicio> listarPorCliente(Long clienteId) {
        return servicioRepo.findByClienteIdOrderByIdDesc(clienteId);
    }

    public List<Servicio> listarPorObrero(Long obreroId) {
        return servicioRepo.findByObreroIdOrderByIdDesc(obreroId);
    }

    @Transactional
    public void iniciarServicio(Long servicioId, Long obreroId) {
        Servicio s = servicioRepo.findByIdAndObreroId(servicioId, obreroId).orElse(null);
        if (s == null) throw new IllegalArgumentException("Servicio no encontrado.");

        if (s.getEstado() != ServicioStatus.PROGRAMADO) {
            throw new IllegalArgumentException("Solo puedes iniciar servicios en estado PROGRAMADO.");
        }

        s.setEstado(ServicioStatus.EN_PROCESO);

        // Guardamos fecha inicio solo si no existe
        if (s.getFechaInicio() == null) {
            s.setFechaInicio(LocalDateTime.now());
        }

        servicioRepo.save(s);
    }

    @Transactional
    public void finalizarServicio(Long servicioId, Long obreroId) {
        Servicio s = servicioRepo.findByIdAndObreroId(servicioId, obreroId).orElse(null);
        if (s == null) throw new IllegalArgumentException("Servicio no encontrado.");

        if (s.getEstado() != ServicioStatus.EN_PROCESO) {
            throw new IllegalArgumentException("Solo puedes finalizar servicios en estado EN_PROCESO.");
        }

        s.setEstado(ServicioStatus.FINALIZADO);
        s.setFechaFin(LocalDateTime.now());

        servicioRepo.save(s);
    }
}
