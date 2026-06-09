from sqlalchemy import Integer, String, DECIMAL
from sqlalchemy.orm import Mapped, mapped_column
from app.database.base import Base


# Esta clase representa la tabla "descuentos"
class DescuentoEntity(Base):
    __tablename__ = "descuentos"

    id_descuento: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    codigo: Mapped[str] = mapped_column(
        String(50),
        unique=True,
        nullable=False
    )

    porcentaje: Mapped[float] = mapped_column(
        DECIMAL(5, 2),
        nullable=False
    )