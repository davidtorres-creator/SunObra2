package com.example.SunObra.marketplace.repository;

import com.example.SunObra.marketplace.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    // Para validar si el obrero ya cotizó
    Optional<Cotizacion> findBySolicitudIdAndObreroId(Long solicitudId, Long obreroId);

    // Para listar cotizaciones de un obrero
    List<Cotizacion> findByObreroIdOrderByIdDesc(Long obreroId);

    // Para que el cliente vea cotizaciones de su solicitud (más adelante)
    List<Cotizacion> findBySolicitudId(Long solicitudId);
}
