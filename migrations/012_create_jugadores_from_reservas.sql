INSERT INTO jugadores (
    id_usuario,
    nombre,
    apellidos,
    peso,
    altura,
    posicion,
    tiro,
    fisico,
    bote,
    pase,
    defensa,
    velocidad
)
SELECT
    i.id_usuario,
    i.nombre,
    i.apellidos,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
FROM inscripciones i
LEFT JOIN jugadores j
    ON j.id_usuario = i.id_usuario
WHERE j.id_jugador IS NULL
    AND i.id_inscripcion = (
        SELECT MIN(i2.id_inscripcion)
        FROM inscripciones i2
        WHERE i2.id_usuario = i.id_usuario
    );
