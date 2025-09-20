package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.User;

/**
 * Repositorio para la entidad User
 * Proporciona métodos para interactuar con la base de datos
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Buscar usuario por email
     * @param email Email del usuario
     * @return Usuario encontrado o vacío
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verificar si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
    
    /**
     * Buscar usuario por email y rol
     * @param email Email del usuario
     * @param rol Rol del usuario
     * @return Usuario encontrado o vacío
     */
    Optional<User> findByEmailAndRol(String email, String rol);
    
    /**
     * Buscar usuario por email, contraseña y rol (para login)
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param rol Rol del usuario
     * @return Usuario encontrado o vacío
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password AND u.rol = :rol AND u.activo = true")
    Optional<User> findByEmailAndPasswordAndRol(@Param("email") String email, 
                                               @Param("password") String password, 
                                               @Param("rol") String rol);
    
    /**
     * Contar usuarios por rol
     * @param rol Rol a contar
     * @return Número de usuarios con ese rol
     */
    long countByRol(String rol);
    
    /**
     * Buscar usuarios activos por rol
     * @param rol Rol del usuario
     * @return Lista de usuarios activos con ese rol
     */
    @Query("SELECT u FROM User u WHERE u.rol = :rol AND u.activo = true ORDER BY u.createdAt DESC")
    java.util.List<User> findActiveUsersByRol(@Param("rol") String rol);
}
