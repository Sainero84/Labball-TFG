CREATE TABLE IF NOT EXISTS tarifas (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    numero_sesiones INT NOT NULL UNIQUE,
    precio_total DECIMAL(10, 2) NOT NULL,
    precio_por_sesion DECIMAL(10, 2) NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tarifas (numero_sesiones, precio_total, precio_por_sesion, activa)
VALUES
    (1, 30.00, 30.00, TRUE),
    (3, 75.00, 25.00, TRUE),
    (5, 100.00, 20.00, TRUE),
    (10, 175.00, 17.50, TRUE),
    (20, 300.00, 15.00, TRUE)
ON DUPLICATE KEY UPDATE
    precio_total = VALUES(precio_total),
    precio_por_sesion = VALUES(precio_por_sesion),
    activa = VALUES(activa);
