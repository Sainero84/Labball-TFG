from sqlalchemy import Integer, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column
from app.database.base import Base


# Tabla intermedia para la relación N:M entre inscripciones y semanas
class InscripcionSemanaEntity(Base):
    """Mapea la entidad ORM inscripcion semana entity con sus columnas y relaciones."""
    __tablename__ = "inscripciones_semanas"

    id_inscripcion: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("inscripciones.id_inscripcion"),
        primary_key=True
    )

    id_semana: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("semanas.id_semana"),
        primary_key=True
    )