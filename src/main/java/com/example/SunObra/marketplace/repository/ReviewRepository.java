package com.example.SunObra.marketplace.repository;


import com.example.SunObra.marketplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByServicioId(Long servicioId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.obreroId = :obreroId")
    Double avgRatingByObreroId(@Param("obreroId") Long obreroId);

    long countByObreroId(Long obreroId);
}

