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
