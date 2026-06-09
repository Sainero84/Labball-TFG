from sqlalchemy import Integer, String, DECIMAL, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.database.base import Base


# Esta clase representa la tabla "jugadores"
class JugadorEntity(Base):
    __tablename__ = "jugadores"

    id_jugador: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    id_usuario: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("usuarios.id_usuario"),
        nullable=False,
        unique=True
    )

    nombre: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    apellidos: Mapped[str] = mapped_column(
        String(150),
        nullable=False
    )

    peso: Mapped[float | None] = mapped_column(
        DECIMAL(5, 2),
        nullable=True
    )

    altura: Mapped[float | None] = mapped_column(
        DECIMAL(4, 2),
        nullable=True
    )

    posicion: Mapped[str | None] = mapped_column(
        String(50),
        nullable=True
    )

    tiro: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    fisico: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    bote: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    pase: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    defensa: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    velocidad: Mapped[int | None] = mapped_column(
        Integer,
        nullable=True
    )

    usuario = relationship("UsuarioEntity")
