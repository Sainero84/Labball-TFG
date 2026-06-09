# Importamos tipos de SQLAlchemy
from sqlalchemy import Integer, String, Text, ForeignKey

# Importamos Mapped y mapped_column
from sqlalchemy.orm import Mapped, mapped_column

# Importamos la base
from app.database.base import Base


# Esta clase representa la tabla "media"
class MediaEntity(Base):
    __tablename__ = "media"

    # Clave primaria
    id_media: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    # FK hacia usuarios
    id_usuario: Mapped[int] = mapped_column(
        Integer,
        ForeignKey("usuarios.id_usuario"),
        nullable=False
    )

    # Título del contenido
    titulo: Mapped[str] = mapped_column(
        String(150),
        nullable=False
    )

    # Descripción opcional
    descripcion: Mapped[str | None] = mapped_column(
        Text,
        nullable=True
    )

    # URL del archivo (imagen o vídeo)
    url_archivo: Mapped[str] = mapped_column(
        String(500),
        nullable=False
    )

    # Mime type
    mime_type : Mapped[str | None] = mapped_column(
        String(500),
        nullable= True
    )
    
    # URL de miniatura (opcional)
    url_miniatura: Mapped[str | None] = mapped_column(
        String(500),
        nullable=True
    )