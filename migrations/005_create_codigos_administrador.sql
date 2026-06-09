CREATE TABLE IF NOT EXISTS codigos_administrador (
    id_codigo_administrador INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(64) NOT NULL UNIQUE
);
