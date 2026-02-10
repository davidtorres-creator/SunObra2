package com.example.SunObra.marketplace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "servicio_id", nullable = false, unique = true)
    private Long servicioId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "obrero_id", nullable = false)
    private Long obreroId;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
