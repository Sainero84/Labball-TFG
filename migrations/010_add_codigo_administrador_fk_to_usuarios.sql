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
