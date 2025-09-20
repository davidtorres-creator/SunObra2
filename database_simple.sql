-- =====================================================
-- SCRIPT SIMPLE DE BASE DE DATOS SUNOBRA
-- Para MySQL Workbench - Versión Mínima
-- =====================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS sunobra_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE sunobra_db;

-- Crear tabla de usuarios (mínima)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL DEFAULT 'cliente',
    telefono VARCHAR(20),
    direccion TEXT,
    preferencias_contacto VARCHAR(50),
    especialidades TEXT,
    experiencia INT DEFAULT 0,
    tarifa_hora DECIMAL(10,2),
    certificaciones TEXT,
    descripcion TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar usuario administrador
INSERT INTO usuarios (nombre, apellido, email, password, user_type, telefono, direccion, preferencias_contacto, activo) VALUES
('Administrador', 'Sistema', 'admin@sunobra.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin', '3138385779', 'Bogotá, Colombia', 'Email', TRUE);

-- Verificar creación
SELECT 'Base de datos SunObra creada exitosamente!' as 'Estado';
SELECT COUNT(*) as 'Usuarios creados' FROM usuarios;
