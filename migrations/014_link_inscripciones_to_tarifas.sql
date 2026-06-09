ALTER TABLE inscripciones
    ADD COLUMN id_tarifa INT NULL AFTER id_usuario;

UPDATE inscripciones i
JOIN tarifas t
    ON t.numero_sesiones = i.numero_sesiones
SET i.id_tarifa = t.id_tarifa
WHERE i.id_tarifa IS NULL;

ALTER TABLE inscripciones
    ADD INDEX idx_inscripciones_id_tarifa (id_tarifa),
    ADD CONSTRAINT fk_inscripciones_tarifas
        FOREIGN KEY (id_tarifa)
        REFERENCES tarifas(id_tarifa);

UPDATE tarifas
SET precio_por_sesion = ROUND(precio_total / numero_sesiones, 2);
