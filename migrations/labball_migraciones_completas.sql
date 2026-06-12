-- Labball - migraciones completas
-- Generado desde migrations/001 a migrations/019


-- ==================================================
-- 001_add_pagado_to_inscripciones.sql
-- ==================================================

ALTER TABLE inscripciones
ADD COLUMN pagado BOOLEAN NOT NULL DEFAULT FALSE;

-- ==================================================
-- 002_add_usuario_fk_to_jugadores.sql
-- ==================================================

ALTER TABLE jugadores
ADD COLUMN id_usuario INT NULL;

-- Si ya existen jugadores, asigna aqui el usuario de cada jugador antes
-- de ejecutar los ALTER TABLE siguientes. Ejemplo:
-- UPDATE jugadores SET id_usuario = 1 WHERE id_jugador = 1;

ALTER TABLE jugadores
MODIFY COLUMN id_usuario INT NOT NULL;

ALTER TABLE jugadores
ADD CONSTRAINT uq_jugadores_id_usuario UNIQUE (id_usuario),
ADD CONSTRAINT fk_jugadores_usuarios
    FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario);

-- ==================================================
-- 003_add_reserva_fields_to_inscripciones.sql
-- ==================================================

ALTER TABLE inscripciones
ADD COLUMN id_usuario INT NULL,
ADD COLUMN numero_sesiones INT NULL,
ADD COLUMN descuento_aplicado DECIMAL(10, 2) NOT NULL DEFAULT 0;

-- Si ya existen inscripciones, asigna aqui un usuario y numero de sesiones
-- antes de ejecutar los ALTER TABLE siguientes. Ejemplo:
-- UPDATE inscripciones SET id_usuario = 1, numero_sesiones = 1 WHERE id_inscripcion = 1;

ALTER TABLE inscripciones
MODIFY COLUMN id_usuario INT NOT NULL,
MODIFY COLUMN numero_sesiones INT NOT NULL;

ALTER TABLE inscripciones
ADD CONSTRAINT fk_inscripciones_usuarios
    FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario);

-- ==================================================
-- 004_create_tarifas.sql
-- ==================================================

CREATE TABLE IF NOT EXISTS tarifas (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    numero_sesiones INT NOT NULL UNIQUE,
    precio_total DECIMAL(10, 2) NOT NULL,
    precio_por_sesion DECIMAL(10, 2) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tarifas (numero_sesiones, precio_total, precio_por_sesion, activa)
VALUES
    (1, 30.00, 30.00, TRUE),
    (3, 75.00, 25.00, TRUE),
    (5, 100.00, 20.00, TRUE),
    (10, 175.00, 17.50, TRUE),
    (20, 300.00, 15.00, TRUE)
ON DUPLICATE KEY UPDATE
    precio_total = VALUES(precio_total),
    precio_por_sesion = VALUES(precio_por_sesion),
    activa = VALUES(activa);

-- ==================================================
-- 005_create_codigos_administrador.sql
-- ==================================================

CREATE TABLE IF NOT EXISTS codigos_administrador (
    id_codigo_administrador INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(64) NOT NULL UNIQUE
);

-- ==================================================
-- 006_simplify_codigos_administrador.sql
-- ==================================================

ALTER TABLE codigos_administrador
    DROP FOREIGN KEY fk_codigos_administrador_usuario_usado;

ALTER TABLE codigos_administrador
    DROP COLUMN activo,
    DROP COLUMN usado,
    DROP COLUMN id_usuario_usado,
    DROP COLUMN fecha_creacion,
    DROP COLUMN fecha_uso;

-- ==================================================
-- 007_add_dia_to_entrenamientos.sql
-- ==================================================

ALTER TABLE entrenamientos
    ADD COLUMN dia DATE NULL AFTER ubicacion;

UPDATE entrenamientos
SET dia = DATE(hora_inicio)
WHERE dia IS NULL;

ALTER TABLE entrenamientos
    MODIFY COLUMN dia DATE NOT NULL;

-- ==================================================
-- 008_remove_dia_from_entrenamientos.sql
-- ==================================================

ALTER TABLE entrenamientos
    DROP COLUMN dia;

-- ==================================================
-- 009_seed_media_test.sql
-- ==================================================

-- Datos de prueba para la tabla media.
-- Usa el usuario administrador existente. Si no existe id_usuario = 2,
-- cambia ese valor por un usuario valido de tu tabla usuarios.

DELETE FROM media
WHERE titulo IN (
    'Tecnica de pase',
    'Control de balon',
    'Finalizacion',
    'Rutina de calentamiento',
    'Estiramientos',
    'Material de entrenamiento'
);

INSERT INTO media (
    id_usuario,
    titulo,
    descripcion,
    url_archivo,
    mime_type,
    url_miniatura
) VALUES
(
    2,
    'Tecnica de pase',
    'Video de prueba para comprobar listado y detalle de media.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_tecnica_pase.png'
),
(
    2,
    'Control de balon',
    'Segundo video de prueba para validar varias filas en Android.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_control_balon.png'
),
(
    2,
    'Finalizacion',
    'Tercer video de prueba para validar detalle y reproductor.',
    'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
    'video/mp4',
    'http://10.0.2.2:8000/static/media-test/thumb_finalizacion.png'
),
(
    2,
    'Rutina de calentamiento',
    'Imagen de prueba para validar contenido no-video en media.',
    'http://10.0.2.2:8000/static/media-test/img_calentamiento.png',
    'image/png',
    NULL
),
(
    2,
    'Estiramientos',
    'Imagen de prueba para validar miniaturas y listado admin.',
    'http://10.0.2.2:8000/static/media-test/img_estiramientos.png',
    'image/png',
    NULL
),
(
    2,
    'Material de entrenamiento',
    'Imagen de prueba para validar registros de tipo imagen.',
    'http://10.0.2.2:8000/static/media-test/img_material.png',
    'image/png',
    NULL
);

-- ==================================================
-- 010_add_codigo_administrador_fk_to_usuarios.sql
-- ==================================================

ALTER TABLE usuarios
    ADD COLUMN id_codigo_administrador INT NULL AFTER es_admin;

UPDATE usuarios u
LEFT JOIN codigos_administrador c
    ON u.codigo_administrador = c.codigo
SET u.id_codigo_administrador = c.id_codigo_administrador
WHERE u.codigo_administrador IS NOT NULL;

ALTER TABLE usuarios
    ADD CONSTRAINT uq_usuarios_id_codigo_administrador
        UNIQUE (id_codigo_administrador);

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_codigo_administrador
        FOREIGN KEY (id_codigo_administrador)
        REFERENCES codigos_administrador(id_codigo_administrador)
        ON DELETE SET NULL;

ALTER TABLE usuarios
    DROP COLUMN codigo_administrador;

-- ==================================================
-- 011_add_reserva_fk_to_entrenamientos.sql
-- ==================================================

ALTER TABLE entrenamientos
    ADD COLUMN id_reserva INT NULL AFTER id_usuario;

UPDATE entrenamientos e
JOIN (
    SELECT id_usuario, MIN(id_inscripcion) AS id_reserva, COUNT(*) AS total_reservas
    FROM inscripciones
    GROUP BY id_usuario
) r
    ON e.id_usuario = r.id_usuario
SET e.id_reserva = r.id_reserva
WHERE r.total_reservas = 1
    AND e.id_reserva IS NULL;

ALTER TABLE entrenamientos
    ADD CONSTRAINT fk_entrenamientos_reserva
        FOREIGN KEY (id_reserva)
        REFERENCES inscripciones(id_inscripcion)
        ON DELETE SET NULL;

-- ==================================================
-- 012_create_jugadores_from_reservas.sql
-- ==================================================

INSERT INTO jugadores (
    id_usuario,
    nombre,
    apellidos,
    peso,
    altura,
    posicion,
    tiro,
    fisico,
    bote,
    pase,
    defensa,
    velocidad
)
SELECT
    i.id_usuario,
    i.nombre,
    i.apellidos,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
FROM inscripciones i
LEFT JOIN jugadores j
    ON j.id_usuario = i.id_usuario
WHERE j.id_jugador IS NULL
    AND i.id_inscripcion = (
        SELECT MIN(i2.id_inscripcion)
        FROM inscripciones i2
        WHERE i2.id_usuario = i.id_usuario
    );

-- ==================================================
-- 013_add_super_admin_and_entrenador_to_usuarios.sql
-- ==================================================

ALTER TABLE usuarios
    ADD COLUMN es_super_admin BOOLEAN NOT NULL DEFAULT FALSE AFTER es_admin,
    ADD COLUMN es_entrenador BOOLEAN NOT NULL DEFAULT FALSE AFTER es_super_admin;

UPDATE usuarios
SET es_super_admin = TRUE
WHERE es_admin = TRUE;

-- ==================================================
-- 014_link_inscripciones_to_tarifas.sql
-- ==================================================

ALTER TABLE inscripciones
    ADD COLUMN id_tarifa INT NULL AFTER id_usuario;

UPDATE inscripciones i
JOIN tarifas t
    ON t.numero_sesiones = i.numero_sesiones
SET i.id_tarifa = t.id_tarifa
WHERE i.id_tarifa IS NULL;

ALTER TABLE inscripciones
    ADD INDEX idx_inscripciones_id_tarifa (id_tarifa),
    ADD CONSTRAINT fk_inscripciones_tarifas
        FOREIGN KEY (id_tarifa)
        REFERENCES tarifas(id_tarifa);

UPDATE tarifas
SET precio_por_sesion = ROUND(precio_total / numero_sesiones, 2);

-- ==================================================
-- 015_entrenamientos_por_inscripcion.sql
-- ==================================================

ALTER TABLE entrenamientos
    ADD COLUMN id_inscripcion INT NULL AFTER id_reserva;

UPDATE entrenamientos
SET id_inscripcion = id_reserva
WHERE id_inscripcion IS NULL
    AND id_reserva IS NOT NULL;

ALTER TABLE entrenamientos
    MODIFY id_jugador INT NULL,
    MODIFY id_usuario INT NULL;

ALTER TABLE entrenamientos
    ADD CONSTRAINT fk_entrenamientos_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES inscripciones(id_inscripcion);

ALTER TABLE inscripciones
    DROP INDEX dni;

-- ==================================================
-- 016_create_catalogos_admin.sql
-- ==================================================

CREATE TABLE IF NOT EXISTS entrenadores (
    id_entrenador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS ubicaciones (
    id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- ==================================================
-- 017_add_profile_fields_to_usuarios.sql
-- ==================================================

ALTER TABLE usuarios
    ADD COLUMN nombre VARCHAR(100) NULL AFTER telefono,
    ADD COLUMN apellido_1 VARCHAR(150) NULL AFTER nombre,
    ADD COLUMN fecha_nacimiento DATE NULL AFTER apellido_1;

-- ==================================================
-- 018_link_catalogos_to_entrenamientos.sql
-- ==================================================

ALTER TABLE entrenadores
    ADD COLUMN id_usuario INT NULL AFTER id_entrenador;

INSERT INTO entrenadores (id_usuario, nombre, activo)
SELECT
    u.id_usuario,
    COALESCE(
        NULLIF(TRIM(CONCAT_WS(' ', u.nombre, u.apellido_1)), ''),
        NULLIF(TRIM(CONCAT_WS(' ', j.nombre, j.apellidos)), ''),
        u.correo
    ) AS nombre,
    TRUE
FROM usuarios u
LEFT JOIN jugadores j
    ON j.id_usuario = u.id_usuario
WHERE u.es_admin = TRUE
    AND u.es_entrenador = TRUE
ON DUPLICATE KEY UPDATE
    id_usuario = COALESCE(entrenadores.id_usuario, VALUES(id_usuario)),
    activo = TRUE;

INSERT INTO entrenadores (id_usuario, nombre, activo)
SELECT
    u.id_usuario,
    u.correo,
    TRUE
FROM usuarios u
LEFT JOIN entrenadores en
    ON en.id_usuario = u.id_usuario
WHERE u.es_admin = TRUE
    AND u.es_entrenador = TRUE
    AND en.id_entrenador IS NULL
ON DUPLICATE KEY UPDATE
    id_usuario = COALESCE(entrenadores.id_usuario, VALUES(id_usuario)),
    activo = TRUE;

ALTER TABLE entrenadores
    ADD CONSTRAINT uq_entrenadores_id_usuario UNIQUE (id_usuario),
    ADD CONSTRAINT fk_entrenadores_usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON DELETE SET NULL;

INSERT INTO entrenadores (nombre, activo)
SELECT DISTINCT TRIM(nombre_entrenador), TRUE
FROM entrenamientos
WHERE nombre_entrenador IS NOT NULL
    AND TRIM(nombre_entrenador) <> ''
ON DUPLICATE KEY UPDATE
    activo = entrenadores.activo;

INSERT INTO ubicaciones (nombre, activo)
SELECT DISTINCT TRIM(ubicacion), TRUE
FROM entrenamientos
WHERE ubicacion IS NOT NULL
    AND TRIM(ubicacion) <> ''
ON DUPLICATE KEY UPDATE
    activo = ubicaciones.activo;

ALTER TABLE entrenamientos
    ADD COLUMN id_entrenador INT NULL AFTER nombre_entrenador,
    ADD COLUMN id_ubicacion INT NULL AFTER ubicacion;

UPDATE entrenamientos e
JOIN entrenadores en
    ON en.nombre = e.nombre_entrenador
SET e.id_entrenador = en.id_entrenador
WHERE e.id_entrenador IS NULL;

UPDATE entrenamientos e
JOIN ubicaciones u
    ON u.nombre = e.ubicacion
SET e.id_ubicacion = u.id_ubicacion
WHERE e.id_ubicacion IS NULL;

ALTER TABLE entrenamientos
    MODIFY COLUMN id_entrenador INT NOT NULL,
    MODIFY COLUMN id_ubicacion INT NOT NULL;

ALTER TABLE entrenamientos
    ADD INDEX idx_entrenamientos_id_entrenador (id_entrenador),
    ADD INDEX idx_entrenamientos_id_ubicacion (id_ubicacion),
    ADD CONSTRAINT fk_entrenamientos_entrenadores
        FOREIGN KEY (id_entrenador)
        REFERENCES entrenadores(id_entrenador),
    ADD CONSTRAINT fk_entrenamientos_ubicaciones
        FOREIGN KEY (id_ubicacion)
        REFERENCES ubicaciones(id_ubicacion);

-- ==================================================
-- 019_simplify_entrenamientos_relations.sql
-- ==================================================

SET @fk_entrenamientos_id_usuario = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'entrenamientos'
        AND COLUMN_NAME = 'id_usuario'
        AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @drop_fk_entrenamientos_id_usuario = IF(
    @fk_entrenamientos_id_usuario IS NOT NULL,
    CONCAT('ALTER TABLE entrenamientos DROP FOREIGN KEY `', @fk_entrenamientos_id_usuario, '`'),
    'SELECT 1'
);

PREPARE stmt FROM @drop_fk_entrenamientos_id_usuario;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_entrenamientos_id_reserva = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'entrenamientos'
        AND COLUMN_NAME = 'id_reserva'
        AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);

SET @drop_fk_entrenamientos_id_reserva = IF(
    @fk_entrenamientos_id_reserva IS NOT NULL,
    CONCAT('ALTER TABLE entrenamientos DROP FOREIGN KEY `', @fk_entrenamientos_id_reserva, '`'),
    'SELECT 1'
);

PREPARE stmt FROM @drop_fk_entrenamientos_id_reserva;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE entrenamientos
    DROP COLUMN nombre_entrenador,
    DROP COLUMN ubicacion,
    DROP COLUMN id_usuario,
    DROP COLUMN id_reserva;
