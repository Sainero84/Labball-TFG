from sqlalchemy import Integer, String, Enum
from sqlalchemy.orm import Mapped, mapped_column
from app.database.base import Base


# Esta clase representa la tabla "semanas"
class SemanaEntity(Base):
    """Mapea la entidad ORM semana entity con sus columnas y relaciones."""
    __tablename__ = "semanas"

    id_semana: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    semana: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    horario: Mapped[str] = mapped_column(
        Enum("AM", "PM", name="horario_semana_enum"),
        nullable=False
    )