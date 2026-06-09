ALTER TABLE jugadores
ADD COLUMN id_usuario INT NULL;

-- Si ya existen jugadores, asigna aqui el usuario de cada jugador antes
-- de ejecutar los ALTER TABLE siguientes. Ejemplo:
-- UPDATE jugadores SET id_usuario = 1 WHERE id_jugador = 1;

ALTER TABLE jugadores
MODIFY COLUMN id_usuario INT NOT NULL;

ALTER TABLE jugadores
ADD CONSTRAINT uq_jugadores_id_usuario UNIQUE (id_usuario),
ADD CONSTRAINT fk_jugadores_usuarios
    FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario);
