package com.example.SunObra.marketplace.repository;

import com.example.SunObra.marketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByServicioId(Long servicioId);

    // ✅ para cargar reviews de varios servicios y armar el mapa servicioId -> rating
    List<Review> findByServicioIdIn(Collection<Long> servicioIds);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.obreroId = :obreroId")
    Double avgRatingByObreroId(@Param("obreroId") Long obreroId);

    long countByObreroId(Long obreroId);

    // ============================
    // ✅ NUEVO: stats para listado de obreros (1 query, sin N+1)
    // ============================

    interface ObreroRatingRow {
        Long getObreroId();
        Double getAvgRating();
        Long getTotal();
    }

    @Query("""
       SELECT r.obreroId AS obreroId,
              AVG(r.rating) AS avgRating,
              COUNT(r.id) AS total
       FROM Review r
       WHERE r.obreroId IN :obreroIds
       GROUP BY r.obreroId
    """)
    List<ObreroRatingRow> statsByObreroIds(@Param("obreroIds") Collection<Long> obreroIds);
}
