from sqlalchemy import Boolean, DECIMAL, Integer
from sqlalchemy.orm import Mapped, mapped_column

from app.database.base import Base


class TarifaEntity(Base):
    """Mapea la entidad ORM tarifa entity con sus columnas y relaciones."""
    __tablename__ = "tarifas"

    id_tarifa: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    numero_sesiones: Mapped[int] = mapped_column(
        Integer,
        unique=True,
        nullable=False
    )

    precio_total: Mapped[float] = mapped_column(
        DECIMAL(10, 2),
        nullable=False
    )

    precio_por_sesion: Mapped[float] = mapped_column(
        DECIMAL(10, 2),
        nullable=False
    )

    activa: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=True
    )
