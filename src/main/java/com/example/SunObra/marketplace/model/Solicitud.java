package com.example.SunObra.marketplace.model;

import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.enums.Urgencia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK a usuarios(id)
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 150)
    private String ubicacion;

    @Column(name = "presupuesto_min", precision = 12, scale = 2)
    private BigDecimal presupuestoMin;

    @Column(name = "presupuesto_max", precision = 12, scale = 2)
    private BigDecimal presupuestoMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgencia urgencia = Urgencia.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SolicitudStatus estado = SolicitudStatus.ABIERTA;

    // Estas columnas las llena la BD (DEFAULT CURRENT_TIMESTAMP)
    // Por eso las marcamos como no editables desde JPA
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
