
-- =====================================================
-- LOGITRACK S.A.
-- DISEÑO FÍSICO - MYSQL
-- Requiere MySQL 8.0.16 o superior
-- =====================================================

CREATE DATABASE IF NOT EXISTS logitrack
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE logitrack;

-- =====================================================
-- 1. CIUDAD
-- =====================================================

CREATE TABLE ciudad (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,

    CONSTRAINT chk_ciudad_id
        CHECK (id BETWEEN 100000 AND 999999)
) ENGINE=InnoDB;

-- =====================================================
-- 2. PERSONA
-- =====================================================

CREATE TABLE persona (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    cedula VARCHAR(20) NOT NULL UNIQUE,

    estado ENUM('PENDIENTE', 'ACTIVO', 'INACTIVO')
        NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT chk_persona_id
        CHECK (id BETWEEN 100000 AND 999999)
) ENGINE=InnoDB;

-- =====================================================
-- 3. ENCARGADO
-- =====================================================

CREATE TABLE encargado (
    id INT UNSIGNED PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    debe_cambiar_password BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_encargado_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 4. ADMIN
-- =====================================================

CREATE TABLE admin (
    id INT UNSIGNED PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    debe_cambiar_password BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_admin_persona
        FOREIGN KEY (id)
        REFERENCES persona(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 5. BODEGA
-- =====================================================

CREATE TABLE bodega (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    ciudad_fk INT UNSIGNED NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    capacidad INT UNSIGNED NOT NULL,
    encargado_fk INT UNSIGNED UNIQUE,

    CONSTRAINT chk_bodega_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_bodega_capacidad
        CHECK (capacidad > 0),

    CONSTRAINT fk_bodega_ciudad
        FOREIGN KEY (ciudad_fk)
        REFERENCES ciudad(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_bodega_encargado
        FOREIGN KEY (encargado_fk)
        REFERENCES encargado(id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 6. MARCA
-- =====================================================

CREATE TABLE marca (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,

    CONSTRAINT chk_marca_id
        CHECK (id BETWEEN 100000 AND 999999)
) ENGINE=InnoDB;

-- =====================================================
-- 7. CATEGORIA_PRODUCTO
-- =====================================================

CREATE TABLE categoria_producto (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,

    CONSTRAINT chk_categoria_id
        CHECK (id BETWEEN 100000 AND 999999)
) ENGINE=InnoDB;

-- =====================================================
-- 8. PRODUCTO
-- =====================================================

CREATE TABLE producto (
    id INT UNSIGNED PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    marca_fk INT UNSIGNED NOT NULL,
    categoria_fk INT UNSIGNED NOT NULL,
    precio DECIMAL(12,2) NOT NULL,

    CONSTRAINT chk_producto_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_producto_precio
        CHECK (precio > 0),

    CONSTRAINT fk_producto_marca
        FOREIGN KEY (marca_fk)
        REFERENCES marca(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_fk)
        REFERENCES categoria_producto(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 9. INVENTARIO_BODEGA
-- =====================================================

CREATE TABLE inventario_bodega (
    id INT UNSIGNED PRIMARY KEY,
    bodega_fk INT UNSIGNED NOT NULL,
    producto_fk INT UNSIGNED NOT NULL,
    stock INT UNSIGNED NOT NULL DEFAULT 0,

    estado ENUM('ACTIVO', 'INACTIVO', 'AGOTADO')
        NOT NULL,

    CONSTRAINT chk_inventario_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_inventario_stock
        CHECK (stock >= 0),

    CONSTRAINT chk_inventario_estado
        CHECK (
            (estado = 'ACTIVO' AND stock > 0)
            OR (estado = 'AGOTADO' AND stock = 0)
            OR (estado = 'INACTIVO')
        ),

    CONSTRAINT uq_bodega_producto
        UNIQUE (bodega_fk, producto_fk),

    CONSTRAINT fk_inventario_bodega
        FOREIGN KEY (bodega_fk)
        REFERENCES bodega(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (producto_fk)
        REFERENCES producto(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 10. MOVIMIENTO_INVENTARIO
-- =====================================================

CREATE TABLE movimiento_inventario (
    id INT UNSIGNED PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    tipo_movimiento ENUM(
        'ENTRADA',
        'SALIDA',
        'TRANSFERENCIA'
    ) NOT NULL,

    usuario_responsable_fk INT UNSIGNED NOT NULL,
    bodega_origen_fk INT UNSIGNED,
    bodega_destino_fk INT UNSIGNED,

    CONSTRAINT chk_movimiento_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_movimiento_bodegas
        CHECK (
            (
                tipo_movimiento = 'ENTRADA'
                AND bodega_origen_fk IS NULL
                AND bodega_destino_fk IS NOT NULL
            )
            OR
            (
                tipo_movimiento = 'SALIDA'
                AND bodega_origen_fk IS NOT NULL
                AND bodega_destino_fk IS NULL
            )
            OR
            (
                tipo_movimiento = 'TRANSFERENCIA'
                AND bodega_origen_fk IS NOT NULL
                AND bodega_destino_fk IS NOT NULL
                AND bodega_origen_fk <> bodega_destino_fk
            )
        ),

    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (usuario_responsable_fk)
        REFERENCES persona(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_movimiento_origen
        FOREIGN KEY (bodega_origen_fk)
        REFERENCES bodega(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_movimiento_destino
        FOREIGN KEY (bodega_destino_fk)
        REFERENCES bodega(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    INDEX idx_movimiento_fecha (fecha),
    INDEX idx_movimiento_usuario (usuario_responsable_fk)
) ENGINE=InnoDB;

-- =====================================================
-- 11. DETALLE_MOVIMIENTO
-- =====================================================

CREATE TABLE detalle_movimiento (
    id INT UNSIGNED PRIMARY KEY,
    movimiento_fk INT UNSIGNED NOT NULL,
    producto_fk INT UNSIGNED NOT NULL,
    cantidad INT UNSIGNED NOT NULL,

    CONSTRAINT chk_detalle_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_detalle_cantidad
        CHECK (cantidad > 0),

    CONSTRAINT uq_movimiento_producto
        UNIQUE (movimiento_fk, producto_fk),

    CONSTRAINT fk_detalle_movimiento
        FOREIGN KEY (movimiento_fk)
        REFERENCES movimiento_inventario(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_fk)
        REFERENCES producto(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
) ENGINE=InnoDB;

-- =====================================================
-- 12. AUDITORIA
-- =====================================================

CREATE TABLE auditoria (
    id INT UNSIGNED PRIMARY KEY,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    tipo_operacion ENUM(
        'INSERT',
        'UPDATE',
        'DELETE'
    ) NOT NULL,

    usuario_fk INT UNSIGNED NOT NULL,
    entidad_afectada VARCHAR(100) NOT NULL,
    registro_afectado_id INT UNSIGNED NOT NULL,

    valores_anteriores JSON,
    valores_nuevos JSON,

    CONSTRAINT chk_auditoria_id
        CHECK (id BETWEEN 100000 AND 999999),

    CONSTRAINT chk_auditoria_registro_id
        CHECK (registro_afectado_id BETWEEN 100000 AND 999999),

    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (usuario_fk)
        REFERENCES persona(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    INDEX idx_auditoria_fecha (fecha_hora),
    INDEX idx_auditoria_usuario (usuario_fk),
    INDEX idx_auditoria_operacion (tipo_operacion)
) ENGINE=InnoDB;



USE logitrack;

-- Administrador inicial de LogiTrack

INSERT INTO persona (
    id,
    nombre,
    correo,
    telefono,
    cedula,
    estado
) VALUES (
    100001,
    'Administrador inicial',
    'admin@logitrack.local',
    NULL,
    '9000000001',
    'ACTIVO'
);

INSERT INTO admin (
    id,
    password,
    debe_cambiar_password
) VALUES (
    100001,
    'REEMPLAZAR_POR_HASH_BCRYPT_VALIDO',
    TRUE
);
