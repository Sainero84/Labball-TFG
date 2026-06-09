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
