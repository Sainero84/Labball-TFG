ALTER TABLE codigos_administrador
    DROP FOREIGN KEY fk_codigos_administrador_usuario_usado;

ALTER TABLE codigos_administrador
    DROP COLUMN activo,
    DROP COLUMN usado,
    DROP COLUMN id_usuario_usado,
    DROP COLUMN fecha_creacion,
    DROP COLUMN fecha_uso;
