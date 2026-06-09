from pydantic import BaseModel, Field
from typing import Optional


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR MEDIA
# --------------------------------------------------

class MediaCreateSchema(BaseModel):
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
    media: list[MediaResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + MEDIA
# --------------------------------------------------

class MediaMessageResponseSchema(BaseModel):
    message: str
    media: MediaResponseSchema


class VideoListItemSchema(BaseModel):
    id_media: int
    titulo: str
    descripcion: Optional[str] = None
    url_miniatura: Optional[str] = None


class VideoListResponseSchema(BaseModel):
    videos: list[VideoListItemSchema]


class VideoDetailResponseSchema(BaseModel):
    id_media: int
    titulo: str
    descripcion: Optional[str] = None
    url_archivo: str
    mime_type: Optional[str] = None
