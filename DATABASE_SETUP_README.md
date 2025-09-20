# 🗄️ CONFIGURACIÓN DE BASE DE DATOS SUNOBRA

## 📋 INSTRUCCIONES PARA MYSQL WORKBENCH

### **PASO 1: PREPARAR MYSQL**
1. Asegúrate de que MySQL esté ejecutándose en tu sistema
2. Abre MySQL Workbench
3. Conéctate a tu servidor MySQL local (puerto 3306)

### **PASO 2: EJECUTAR EL SCRIPT**

#### **Opción A: Script Completo (Recomendado)**
1. Abre el archivo `database_setup.sql` en MySQL Workbench
2. Ejecuta todo el script (Ctrl+Shift+Enter)
3. Este script crea:
   - Base de datos `sunobra_db`
   - Tabla `usuarios` con todos los campos necesarios
   - Tablas adicionales para futuras funcionalidades
   - Datos de ejemplo
   - Vistas y procedimientos almacenados

#### **Opción B: Script Simple (Mínimo)**
1. Si prefieres algo más simple, usa `database_simple.sql`
2. Este script solo crea la tabla básica de usuarios

### **PASO 3: VERIFICAR CONFIGURACIÓN**

#### **Verificar Base de Datos:**
```sql
SHOW DATABASES;
USE sunobra_db;
SHOW TABLES;
```

#### **Verificar Usuarios:**
```sql
SELECT * FROM usuarios;
```

### **PASO 4: CONFIGURAR CONTRASEÑA (OPCIONAL)**

Si tu MySQL tiene contraseña, actualiza el archivo `application.properties`:

```properties
spring.datasource.password=tu_contraseña_aqui
```

### **PASO 5: PROBAR LA APLICACIÓN**

1. Ejecuta: `mvn spring-boot:run`
2. Abre tu navegador en: `http://localhost:8000`
3. Deberías ver la página principal de SunObra

## 🔧 CONFIGURACIÓN ACTUAL

### **Base de Datos:**
- **Nombre:** `sunobra_db`
- **Usuario:** `root`
- **Contraseña:** (vacía)
- **Puerto:** `3306`
- **Charset:** `utf8mb4`

### **Tablas Creadas:**
- `usuarios` - Usuarios del sistema (clientes, obreros, admin)
- `proyectos` - Proyectos de construcción
- `contrataciones` - Relaciones cliente-obrero
- `configuracion` - Configuración del sistema

### **Usuarios de Prueba:**
- **Admin:** `admin@sunobra.com` / contraseña: `admin123`
- **Obrero:** `juan.perez@email.com` / contraseña: `admin123`
- **Cliente:** `carlos.lopez@email.com` / contraseña: `admin123`

## 🚨 SOLUCIÓN DE PROBLEMAS

### **Error: "Access denied for user 'root'@'localhost'"**
- Verifica que MySQL esté ejecutándose
- Confirma que el usuario `root` no tenga contraseña
- O actualiza la contraseña en `application.properties`

### **Error: "Unknown database 'sunobra_db'"**
- Ejecuta el script SQL completo
- Verifica que la base de datos se haya creado correctamente

### **Error: "Table 'usuarios' doesn't exist"**
- Verifica que hayas ejecutado el script SQL
- Confirma que estés usando la base de datos correcta

## 📊 ESTRUCTURA DE LA TABLA USUARIOS

```sql
CREATE TABLE usuarios (
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
);
```

## ✅ VERIFICACIÓN FINAL

Después de ejecutar el script, deberías ver:
- ✅ Base de datos `sunobra_db` creada
- ✅ Tabla `usuarios` con estructura completa
- ✅ Usuario administrador creado
- ✅ Usuarios de ejemplo insertados
- ✅ Aplicación Spring Boot funcionando en puerto 8000

## 🆘 SOPORTE

Si tienes problemas:
1. Verifica que MySQL esté ejecutándose
2. Confirma que el puerto 3306 esté disponible
3. Revisa los logs de la aplicación Spring Boot
4. Ejecuta el script simple si el completo falla
