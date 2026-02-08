package com.example.SunObra.marketplace.service;

import com.example.SunObra.marketplace.enums.Urgencia;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SolicitudCreateRequest {
    private String especialidad;
    private String descripcion;
    private String ubicacion;
    private BigDecimal presupuestoMin;
    private BigDecimal presupuestoMax;
    private Urgencia urgencia = Urgencia.MEDIA;
}
