# Este archivo guarda la configuración de conexión a MySQL.
# De momento lo hacemos con valores escritos directamente
# para entenderlo bien. Más adelante podríamos moverlo a variables
# de entorno o a un archivo .env.

import os

DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "12345")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "3306")
DB_NAME = os.getenv("DB_NAME", "tfg_db")
