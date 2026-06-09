from datetime import date

from sqlalchemy import Integer, String, DECIMAL, Date, ForeignKey, Boolean
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database.base import Base


# Esta clase representa la tabla "inscripciones"
class InscripcionEntity(Base):
    __tablename__ = "inscripciones"

    id_inscripcion: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    id_usuario: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("usuarios.id_usuario"),
        nullable=False
    )

    id_tarifa: Mapped[int | None] = mapped_column(
        Integer,
        ForeignKey("tarifas.id_tarifa"),
        nullable=True
    )

    numero_sesiones: Mapped[int] = mapped_column(
        Integer,
        nullable=False
    )

    nombre: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    apellidos: Mapped[str] = mapped_column(
        String(150),
        nullable=False
    )

    dni: Mapped[str] = mapped_column(
        String(20),
        nullable=False
    )

    fecha_nacimiento: Mapped[date] = mapped_column(
        Date,
        nullable=False
    )

    correo: Mapped[str] = mapped_column(
        String(150),
        nullable=False
    )

    telefono: Mapped[str | None] = mapped_column(
        String(20),
        nullable=True
    )

    club: Mapped[str | None] = mapped_column(
        String(150),
        nullable=True
    )

    categoria: Mapped[str | None] = mapped_column(
        String(100),
        nullable=True
    )

    precio_sin_descuento: Mapped[float] = mapped_column(
        DECIMAL(10, 2),
        nullable=False
    )

    descuento_aplicado: Mapped[float] = mapped_column(
        DECIMAL(10, 2),
        nullable=False,
        default=0
    )

    precio_final: Mapped[float] = mapped_column(
        DECIMAL(10, 2),
        nullable=False
    )

    pagado: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=False
    )

    # FK opcional hacia descuentos
    id_descuento: Mapped[int | None] = mapped_column(
        Integer,
        ForeignKey("descuentos.id_descuento"),
        nullable=True
    )

    usuario = relationship("UsuarioEntity")
    tarifa = relationship("TarifaEntity")
    descuento = relationship("DescuentoEntity")
