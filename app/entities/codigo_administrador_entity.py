from sqlalchemy import Integer, String
from sqlalchemy.orm import Mapped, mapped_column

from app.database.base import Base


class CodigoAdministradorEntity(Base):
    """Mapea la entidad ORM codigo administrador entity con sus columnas y relaciones."""
    __tablename__ = "codigos_administrador"

    id_codigo_administrador: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    codigo: Mapped[str] = mapped_column(
        String(64),
        unique=True,
        nullable=False
    )
