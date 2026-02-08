package com.example.SunObra.marketplace.model;

import com.example.SunObra.marketplace.enums.CotizacionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "cotizaciones",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"solicitud_id", "obrero_id"})
        }
)
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    @Column(name = "obrero_id", nullable = false)
    private Long obreroId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "tiempo_estimado_dias")
    private Integer tiempoEstimadoDias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CotizacionStatus estado = CotizacionStatus.ENVIADA;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

