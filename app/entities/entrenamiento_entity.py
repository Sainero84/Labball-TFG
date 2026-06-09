from sqlalchemy import Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime

from app.database.base import Base


class EntrenamientoEntity(Base):
    __tablename__ = "entrenamientos"

    id_entrenamiento: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    nombre_entrenador: Mapped[str] = mapped_column(
        String(150),
        nullable=False
    )

    ubicacion: Mapped[str] = mapped_column(
        String(200),
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

    id_usuario: Mapped[int | None] = mapped_column(
        ForeignKey("usuarios.id_usuario"),
        nullable=True
    )

    id_reserva: Mapped[int | None] = mapped_column(
        ForeignKey("inscripciones.id_inscripcion"),
        nullable=True
    )

    id_inscripcion: Mapped[int | None] = mapped_column(
        ForeignKey("inscripciones.id_inscripcion"),
        nullable=True
    )

    jugador = relationship("JugadorEntity")
    usuario = relationship("UsuarioEntity")
    inscripcion = relationship(
        "InscripcionEntity",
        foreign_keys=[id_inscripcion]
    )
    reserva_legacy = relationship(
        "InscripcionEntity",
        foreign_keys=[id_reserva],
        viewonly=True
    )
