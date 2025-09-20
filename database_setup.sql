-- =====================================================
-- SCRIPT DE CONFIGURACIÓN DE BASE DE DATOS SUNOBRA
-- Para MySQL Workbench
-- =====================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS sunobra_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE sunobra_db;

-- =====================================================
-- TABLA DE USUARIOS
-- =====================================================
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
    
    -- Campos específicos para obreros
    especialidades TEXT,
    experiencia INT DEFAULT 0,
    tarifa_hora DECIMAL(10,2),
    certificaciones TEXT,
    descripcion TEXT,
    
    -- Campos de control
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    
    -- Índices
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE PROYECTOS (para futuras funcionalidades)
-- =====================================================
CREATE TABLE IF NOT EXISTS proyectos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100),
    imagen_url VARCHAR(500),
    ubicacion VARCHAR(255),
    cliente_id BIGINT,
    obrero_id BIGINT,
    estado VARCHAR(50) DEFAULT 'pendiente',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio DATE,
    fecha_fin DATE,
    presupuesto DECIMAL(12,2),
    
    -- Índices
    INDEX idx_cliente (cliente_id),
    INDEX idx_obrero (obrero_id),
    INDEX idx_estado (estado),
    INDEX idx_categoria (categoria),
    
    -- Claves foráneas
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (obrero_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE CONTRATACIONES (para futuras funcionalidades)
-- =====================================================
CREATE TABLE IF NOT EXISTS contrataciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    obrero_id BIGINT NOT NULL,
    proyecto_id BIGINT,
    fecha_contratacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio DATE,
    fecha_fin DATE,
    tarifa_total DECIMAL(12,2),
    estado VARCHAR(50) DEFAULT 'activa',
    descripcion TEXT,
    calificacion_cliente INT CHECK (calificacion_cliente >= 1 AND calificacion_cliente <= 5),
    calificacion_obrero INT CHECK (calificacion_obrero >= 1 AND calificacion_obrero <= 5),
    comentarios_cliente TEXT,
    comentarios_obrero TEXT,
    
    -- Índices
    INDEX idx_cliente (cliente_id),
    INDEX idx_obrero (obrero_id),
    INDEX idx_proyecto (proyecto_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_contratacion (fecha_contratacion),
    
    -- Claves foráneas
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (obrero_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE CONFIGURACIÓN DEL SISTEMA
-- =====================================================
CREATE TABLE IF NOT EXISTS configuracion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT,
    descripcion TEXT,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INSERTAR DATOS INICIALES
-- =====================================================

-- Insertar configuración inicial del sistema
INSERT INTO configuracion (clave, valor, descripcion) VALUES
('logo_url', '/images/logo.png', 'URL del logo del sitio'),
('site_name', 'SunObra', 'Nombre del sitio web'),
('site_description', 'Plataforma de Servicios de Construcción', 'Descripción del sitio'),
('contact_phone', '3138385779', 'Teléfono de contacto'),
('contact_email', 'sunobra69@gmail.com', 'Email de contacto'),
('contact_address', 'Bogotá, Colombia', 'Dirección de contacto'),
('maintenance_mode', 'false', 'Modo de mantenimiento del sitio');

-- Insertar usuario administrador por defecto
INSERT INTO usuarios (nombre, apellido, email, password, user_type, telefono, direccion, preferencias_contacto, activo) VALUES
('Administrador', 'Sistema', 'admin@sunobra.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin', '3138385779', 'Bogotá, Colombia', 'Email', TRUE);

-- Insertar algunos usuarios de ejemplo
INSERT INTO usuarios (nombre, apellido, email, password, user_type, telefono, direccion, preferencias_contacto, especialidades, experiencia, tarifa_hora, descripcion, activo) VALUES
('Juan', 'Pérez', 'juan.perez@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'obrero', '3001234567', 'Bogotá, Colombia', 'Teléfono', 'Albañilería,Construcción', 5, 25000.00, 'Especialista en construcción de viviendas y remodelaciones', TRUE),
('María', 'González', 'maria.gonzalez@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'obrero', '3002345678', 'Medellín, Colombia', 'Ambos', 'Electricidad,Plomería', 8, 30000.00, 'Técnica en instalaciones eléctricas y de plomería', TRUE),
('Carlos', 'López', 'carlos.lopez@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'cliente', '3003456789', 'Cali, Colombia', 'Email', NULL, NULL, NULL, NULL, TRUE),
('Ana', 'Martínez', 'ana.martinez@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'cliente', '3004567890', 'Barranquilla, Colombia', 'Teléfono', NULL, NULL, NULL, NULL, TRUE);

-- Insertar algunos proyectos de ejemplo
INSERT INTO proyectos (titulo, descripcion, categoria, imagen_url, ubicacion, cliente_id, estado, presupuesto) VALUES
('Remodelación de Cocina', 'Remodelación completa de cocina con nuevos gabinetes y electrodomésticos', 'Remodelación', '/images/gallary-1.jpg', 'Bogotá, Colombia', 3, 'disponible', 5000000.00),
('Construcción de Local Comercial', 'Construcción de local comercial de 50m² con acabados de primera', 'Construcción', '/images/gallary-2.jpg', 'Medellín, Colombia', 4, 'en_progreso', 15000000.00),
('Reparación de Fachada', 'Reparación y pintura de fachada de edificio residencial', 'Reparación', '/images/gallary-3.jpg', 'Cali, Colombia', 3, 'completado', 3000000.00);

-- =====================================================
-- CREAR VISTAS ÚTILES
-- =====================================================

-- Vista de obreros activos con sus especialidades
CREATE VIEW vista_obreros_activos AS
SELECT 
    u.id,
    u.nombre,
    u.apellido,
    u.email,
    u.telefono,
    u.direccion,
    u.especialidades,
    u.experiencia,
    u.tarifa_hora,
    u.descripcion,
    u.fecha_registro
FROM usuarios u
WHERE u.user_type = 'obrero' 
AND u.activo = TRUE;

-- Vista de clientes activos
CREATE VIEW vista_clientes_activos AS
SELECT 
    u.id,
    u.nombre,
    u.apellido,
    u.email,
    u.telefono,
    u.direccion,
    u.preferencias_contacto,
    u.fecha_registro
FROM usuarios u
WHERE u.user_type = 'cliente' 
AND u.activo = TRUE;

-- Vista de proyectos con información de cliente
CREATE VIEW vista_proyectos_completa AS
SELECT 
    p.id,
    p.titulo,
    p.descripcion,
    p.categoria,
    p.imagen_url,
    p.ubicacion,
    p.estado,
    p.presupuesto,
    p.fecha_creacion,
    p.fecha_inicio,
    p.fecha_fin,
    CONCAT(u.nombre, ' ', u.apellido) as cliente_nombre,
    u.email as cliente_email,
    u.telefono as cliente_telefono
FROM proyectos p
LEFT JOIN usuarios u ON p.cliente_id = u.id;

-- =====================================================
-- CREAR PROCEDIMIENTOS ALMACENADOS
-- =====================================================

-- Procedimiento para buscar obreros por especialidad
DELIMITER //
CREATE PROCEDURE BuscarObrerosPorEspecialidad(IN especialidad_buscar VARCHAR(100))
BEGIN
    SELECT 
        u.id,
        u.nombre,
        u.apellido,
        u.email,
        u.telefono,
        u.especialidades,
        u.experiencia,
        u.tarifa_hora,
        u.descripcion
    FROM usuarios u
    WHERE u.user_type = 'obrero' 
    AND u.activo = TRUE
    AND u.especialidades LIKE CONCAT('%', especialidad_buscar, '%')
    ORDER BY u.experiencia DESC, u.tarifa_hora ASC;
END //
DELIMITER ;

-- Procedimiento para obtener estadísticas del sistema
DELIMITER //
CREATE PROCEDURE ObtenerEstadisticasSistema()
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM usuarios WHERE user_type = 'cliente' AND activo = TRUE) as total_clientes,
        (SELECT COUNT(*) FROM usuarios WHERE user_type = 'obrero' AND activo = TRUE) as total_obreros,
        (SELECT COUNT(*) FROM proyectos WHERE estado = 'disponible') as proyectos_disponibles,
        (SELECT COUNT(*) FROM proyectos WHERE estado = 'en_progreso') as proyectos_en_progreso,
        (SELECT COUNT(*) FROM proyectos WHERE estado = 'completado') as proyectos_completados,
        (SELECT AVG(tarifa_hora) FROM usuarios WHERE user_type = 'obrero' AND activo = TRUE AND tarifa_hora IS NOT NULL) as tarifa_promedio;
END //
DELIMITER ;

-- =====================================================
-- CREAR TRIGGERS
-- =====================================================

-- Trigger para actualizar fecha de modificación en usuarios
DELIMITER //
CREATE TRIGGER tr_usuarios_update
BEFORE UPDATE ON usuarios
FOR EACH ROW
BEGIN
    SET NEW.fecha_registro = OLD.fecha_registro; -- Mantener fecha original
END //
DELIMITER ;

-- =====================================================
-- CONFIGURAR PERMISOS (OPCIONAL)
-- =====================================================

-- Crear usuario específico para la aplicación (opcional)
-- CREATE USER 'sunobra_user'@'localhost' IDENTIFIED BY 'sunobra_password_2024';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON sunobra_db.* TO 'sunobra_user'@'localhost';
-- FLUSH PRIVILEGES;

-- =====================================================
-- VERIFICAR INSTALACIÓN
-- =====================================================

-- Mostrar información de las tablas creadas
SELECT 
    TABLE_NAME as 'Tabla',
    TABLE_ROWS as 'Filas',
    CREATE_TIME as 'Fecha Creación'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'sunobra_db'
ORDER BY TABLE_NAME;

-- Mostrar usuarios creados
SELECT 
    id,
    nombre,
    apellido,
    email,
    user_type,
    activo,
    fecha_registro
FROM usuarios
ORDER BY user_type, nombre;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

-- Mensaje de confirmación
SELECT 'Base de datos SunObra configurada exitosamente!' as 'Estado';
