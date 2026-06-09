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
