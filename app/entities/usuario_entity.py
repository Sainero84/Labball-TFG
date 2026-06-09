# Importamos los tipos de columna que vamos a usar
from sqlalchemy import Boolean, Date, ForeignKey, Integer, String

# Importamos Mapped y mapped_column para definir columnas tipadas
from sqlalchemy.orm import Mapped, mapped_column, relationship

# Importamos la clase Base común de SQLAlchemy
from app.database.base import Base


# Esta clase representa la tabla "usuarios"
class UsuarioEntity(Base):
    # Nombre real de la tabla en MySQL
    __tablename__ = "usuarios"

    # Clave primaria autoincremental
    id_usuario: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    #Token Firebase
    firebase_uid: Mapped[str] = mapped_column(
        String(128),
        unique=True,
        nullable=False
    )

    # Correo único del usuario
    correo: Mapped[str] = mapped_column(
        String(150),
        unique=True,
        nullable=False
    )

    # Indica si el usuario es administrador
    es_admin: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=False,
        server_default="0"
    )

    # Indica si el usuario puede gestionar roles de otros usuarios
    es_super_admin: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=False,
        server_default="0"
    )

    # Indica si el usuario es entrenador
    es_entrenador: Mapped[bool] = mapped_column(
        Boolean,
        nullable=False,
        default=False,
        server_default="0"
    )

    # Nuevo campo que quieres añadir
    # Será opcional porque no todos los usuarios serán administradores
    id_codigo_administrador: Mapped[int | None] = mapped_column(
        Integer,
        ForeignKey("codigos_administrador.id_codigo_administrador"),
        unique=True,
        nullable=True
    )

    codigo_administrador_rel = relationship("CodigoAdministradorEntity")
    jugador = relationship("JugadorEntity", uselist=False)

    # Teléfono opcional
    telefono: Mapped[str | None] = mapped_column(
        String(20),
        nullable=True
    )

    nombre: Mapped[str | None] = mapped_column(
        String(100),
        nullable=True
    )

    apellido_1: Mapped[str | None] = mapped_column(
        String(150),
        nullable=True
    )

    fecha_nacimiento: Mapped[Date | None] = mapped_column(
        Date,
        nullable=True
    )

    # URL de imagen de perfil
    foto_perfil_url: Mapped[str | None] = mapped_column(
        String(500),
        nullable=True
    )

    # Tipo MIME de la imagen de perfil
    foto_perfil_mime_type: Mapped[str | None] = mapped_column(
        String(100),
        nullable=True
    )

    
