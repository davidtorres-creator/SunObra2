package com.example.SunObra.marketplace.repository;

import com.example.SunObra.marketplace.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SunObra.marketplace.enums.ServicioStatus;

import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    Optional<Servicio> findBySolicitudId(Long solicitudId);

    Optional<Servicio> findByIdAndObreroId(Long id, Long obreroId);

    List<Servicio> findByClienteIdOrderByIdDesc(Long clienteId);

    List<Servicio> findByObreroIdOrderByIdDesc(Long obreroId);

    // src/main/java/com/example/SunObra/marketplace/repository/ServicioRepository.java
    /**
     * Cuenta los servicios según su estado.
     * @param estado estado a contar.
     * @return número de servicios en ese estado.
     */
    long countByEstado(ServicioStatus estado);

}

