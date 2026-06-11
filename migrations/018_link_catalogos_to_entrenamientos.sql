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
