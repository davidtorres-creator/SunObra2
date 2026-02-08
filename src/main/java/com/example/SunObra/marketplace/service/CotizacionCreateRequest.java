package com.example.SunObra.marketplace.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CotizacionCreateRequest {
    private BigDecimal precio;
    private String mensaje;
    private Integer tiempoEstimadoDias;
}

