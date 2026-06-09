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
