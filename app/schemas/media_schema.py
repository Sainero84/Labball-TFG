from pydantic import BaseModel, Field
from typing import Optional


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR MEDIA
# --------------------------------------------------

class MediaCreateSchema(BaseModel):
    """Define el esquema de datos media create schema para validar entradas y respuestas."""
    id_usuario: int
    titulo: str = Field(..., min_length=1, max_length=150)
    descripcion: Optional[str] = None
    url_archivo: str = Field(..., min_length=1, max_length=500)
    url_miniatura: Optional[str] = Field(default=None, max_length=500)
    mime_type: Optional[str] = Field(default=None, max_length=100)


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR MEDIA
# --------------------------------------------------

class MediaUpdateSchema(BaseModel):
    """Define el esquema de datos media update schema para validar entradas y respuestas."""
    id_usuario: Optional[int] = None
    titulo: Optional[str] = Field(default=None, min_length=1, max_length=150)
    descripcion: Optional[str] = None
    url_archivo: Optional[str] = Field(default=None, min_length=1, max_length=500)
    url_miniatura: Optional[str] = Field(default=None, max_length=500)
    mime_type: Optional[str] = Field(default=None, max_length=100)


# --------------------------------------------------
# SCHEMA DE SALIDA: MEDIA
# --------------------------------------------------

class MediaResponseSchema(BaseModel):
    """Define el esquema de datos media response schema para validar entradas y respuestas."""
    id_media: int
    id_usuario: int
    titulo: str
    descripcion: Optional[str] = None
    url_archivo: str
    url_miniatura: Optional[str] = None
    mime_type: Optional[str] = None


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE MEDIA
# --------------------------------------------------

class MediaListResponseSchema(BaseModel):
    """Define el esquema de datos media list response schema para validar entradas y respuestas."""
    media: list[MediaResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + MEDIA
# --------------------------------------------------

class MediaMessageResponseSchema(BaseModel):
    """Define el esquema de datos media message response schema para validar entradas y respuestas."""
    message: str
    media: MediaResponseSchema


class VideoListItemSchema(BaseModel):
    """Define el esquema de datos video list item schema para validar entradas y respuestas."""
    id_media: int
    titulo: str
    descripcion: Optional[str] = None
    url_miniatura: Optional[str] = None


class VideoListResponseSchema(BaseModel):
    """Define el esquema de datos video list response schema para validar entradas y respuestas."""
    videos: list[VideoListItemSchema]


class VideoDetailResponseSchema(BaseModel):
    """Define el esquema de datos video detail response schema para validar entradas y respuestas."""
    id_media: int
    titulo: str
    descripcion: Optional[str] = None
    url_archivo: str
    mime_type: Optional[str] = None
