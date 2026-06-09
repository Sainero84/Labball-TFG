ALTER TABLE usuarios
    ADD COLUMN nombre VARCHAR(100) NULL AFTER telefono,
    ADD COLUMN apellido_1 VARCHAR(150) NULL AFTER nombre,
    ADD COLUMN fecha_nacimiento DATE NULL AFTER apellido_1;
