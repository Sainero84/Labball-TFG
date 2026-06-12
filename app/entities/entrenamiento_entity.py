from sqlalchemy import Integer, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime

from app.database.base import Base


class EntrenamientoEntity(Base):
    """Mapea la entidad ORM entrenamiento entity con sus columnas y relaciones."""
    __tablename__ = "entrenamientos"

    id_entrenamiento: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    id_entrenador: Mapped[int] = mapped_column(
        ForeignKey("entrenadores.id_entrenador"),
        nullable=False
    )

    id_ubicacion: Mapped[int] = mapped_column(
        ForeignKey("ubicaciones.id_ubicacion"),
        nullable=False
    )

    hora_inicio: Mapped[datetime] = mapped_column(
        nullable=False
    )

    hora_fin: Mapped[datetime] = mapped_column(
        nullable=False
    )

    id_jugador: Mapped[int | None] = mapped_column(
        ForeignKey("jugadores.id_jugador"),
        nullable=True
    )

    id_inscripcion: Mapped[int | None] = mapped_column(
        ForeignKey("inscripciones.id_inscripcion"),
        nullable=True
    )

    jugador = relationship("JugadorEntity")
    entrenador = relationship("EntrenadorEntity")
    ubicacion_catalogo = relationship("UbicacionEntity")
    inscripcion = relationship(
        "InscripcionEntity",
        foreign_keys=[id_inscripcion]
    )
