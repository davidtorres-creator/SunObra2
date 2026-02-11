package com.example.SunObra.marketplace.service;


import com.example.SunObra.marketplace.enums.ServicioStatus;
import com.example.SunObra.marketplace.model.Review;
import com.example.SunObra.marketplace.model.Servicio;
import com.example.SunObra.marketplace.repository.ReviewRepository;
import com.example.SunObra.marketplace.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ServicioRepository servicioRepo;

    public ReviewService(ReviewRepository reviewRepo, ServicioRepository servicioRepo) {
        this.reviewRepo = reviewRepo;
        this.servicioRepo = servicioRepo;
    }

    public Review buscarPorServicio(Long servicioId) {
        return reviewRepo.findByServicioId(servicioId).orElse(null);
    }

    public Map<Long, Integer> ratingsPorServicioIds(List<Long> servicioIds) {
        if (servicioIds == null || servicioIds.isEmpty()) return Map.of();

        return reviewRepo.findByServicioIdIn(servicioIds)
                .stream()
                .collect(Collectors.toMap(
                        Review::getServicioId,
                        Review::getRating
                ));
    }


    @Transactional
    public void crearReview(Long clienteId, Long servicioId, ReviewCreateRequest req) {
        validar(req);

        Servicio servicio = servicioRepo.findById(servicioId).orElse(null);
        if (servicio == null) throw new IllegalArgumentException("Servicio no existe.");

        if (!servicio.getClienteId().equals(clienteId)) {
            throw new IllegalArgumentException("No tienes permiso para calificar este servicio.");
        }

        if (servicio.getEstado() != ServicioStatus.FINALIZADO) {
            throw new IllegalArgumentException("Solo puedes calificar un servicio FINALIZADO.");
        }

        if (reviewRepo.findByServicioId(servicioId).isPresent()) {
            throw new IllegalArgumentException("Este servicio ya fue calificado.");
        }

        Review r = new Review();
        r.setServicioId(servicioId);
        r.setClienteId(clienteId);
        r.setObreroId(servicio.getObreroId());
        r.setRating(req.getRating());
        r.setComentario(req.getComentario() != null ? req.getComentario().trim() : null);

        reviewRepo.save(r);
    }

    private void validar(ReviewCreateRequest req) {
        if (req.getRating() == null) throw new IllegalArgumentException("El rating es obligatorio.");
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("El rating debe estar entre 1 y 5.");
        }
        if (req.getComentario() != null && req.getComentario().length() > 1000) {
            throw new IllegalArgumentException("El comentario es muy largo (máx 1000 caracteres).");
        }
    }
}
