ALTER TABLE entrenamientos
    ADD COLUMN dia DATE NULL AFTER ubicacion;

UPDATE entrenamientos
SET dia = DATE(hora_inicio)
WHERE dia IS NULL;

ALTER TABLE entrenamientos
    MODIFY COLUMN dia DATE NOT NULL;
