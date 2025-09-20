package com.example.repository;

import com.example.model.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<usuarios, Long> {

    /**
     * Buscar usuario por email
     */
    Optional<usuarios> findByEmail(String email);

    /**
     * Buscar usuario por email y tipo de usuario
     */
    @Query("SELECT u FROM usuarios u WHERE u.email = :email AND u.userType = :userType")
    Optional<usuarios> findByEmailAndUserType(@Param("email") String email, 
                                             @Param("userType") String userType);

    /**
     * Verificar si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuarios por tipo
     */
    @Query("SELECT u FROM usuarios u WHERE u.userType = :userType")
    java.util.List<usuarios> findByUserType(@Param("userType") String userType);
}