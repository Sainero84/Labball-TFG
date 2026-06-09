ALTER TABLE usuarios
    ADD COLUMN es_super_admin BOOLEAN NOT NULL DEFAULT FALSE AFTER es_admin,
    ADD COLUMN es_entrenador BOOLEAN NOT NULL DEFAULT FALSE AFTER es_super_admin;

UPDATE usuarios
SET es_super_admin = TRUE
WHERE es_admin = TRUE;
