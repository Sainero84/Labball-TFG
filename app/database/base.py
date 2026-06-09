# DeclarativeBase es la clase base moderna de SQLAlchemy
# para definir modelos ORM.
from sqlalchemy.orm import DeclarativeBase


# Esta clase base será heredada por todas nuestras entidades.
# Piensa en ella como la "clase madre" de los modelos de base de datos.
class Base(DeclarativeBase):
    pass