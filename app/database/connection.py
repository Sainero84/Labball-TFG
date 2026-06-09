# Importamos create_engine, que sirve para crear la conexión principal
# hacia la base de datos.
from sqlalchemy import create_engine

# Importamos sessionmaker, que nos permitirá crear sesiones.
# Una sesión es el objeto con el que haremos operaciones contra la base de datos.
from sqlalchemy.orm import sessionmaker

# Importamos los datos de configuración desde config.py
from app.database.config import DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME


# --------------------------------------------------
# CONSTRUIR LA URL DE CONEXIÓN
# --------------------------------------------------

# Aquí construimos la URL que SQLAlchemy utilizará para conectarse a MySQL.
# Formato:
# mysql+pymysql://usuario:password@host:puerto/base_de_datos
DATABASE_URL = f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"


# --------------------------------------------------
# CREAR EL ENGINE
# --------------------------------------------------

# El engine es el objeto principal que representa la conexión con la base de datos.
# No es una conexión abierta permanente "una a una", sino una fábrica/configuración
# que SQLAlchemy usa para gestionar conexiones reales cuando hacen falta.
engine = create_engine(
    DATABASE_URL,
    echo=True
)


# --------------------------------------------------
# CREAR SESSIONLOCAL
# --------------------------------------------------

# sessionmaker crea una "fábrica de sesiones".
# Cada vez que necesitemos hablar con la base de datos, crearemos una sesión.
SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)


# --------------------------------------------------
# FUNCIÓN PARA OBTENER UNA SESIÓN
# --------------------------------------------------

def get_db():
    # Creamos una sesión nueva
    db = SessionLocal()

    try:
        # La devolvemos para que otros componentes la usen
        yield db
    finally:
        # Pase lo que pase, cerramos la sesión al final
        db.close()