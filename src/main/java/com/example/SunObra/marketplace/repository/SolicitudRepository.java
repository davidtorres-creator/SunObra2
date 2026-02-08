package com.example.SunObra.marketplace.repository;

import com.example.SunObra.marketplace.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Para: "mis solicitudes" del cliente
    List<Solicitud> findByClienteIdOrderByIdDesc(Long clienteId);

    // Para evitar que un cliente vea solicitudes de otro
    Optional<Solicitud> findByIdAndClienteId(Long id, Long clienteId);
}
