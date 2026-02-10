package com.example.SunObra.marketplace.service;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private Integer rating;
    private String comentario;
}
