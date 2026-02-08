package com.example.SunObra.marketplace.repository;

import com.example.SunObra.marketplace.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    Optional<Servicio> findBySolicitudId(Long solicitudId);

    List<Servicio> findByClienteIdOrderByIdDesc(Long clienteId);

    List<Servicio> findByObreroIdOrderByIdDesc(Long obreroId);
}

