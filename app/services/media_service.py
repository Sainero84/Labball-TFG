import os
from urllib.parse import urlparse

from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import media_repository, usuario_repository
from app.schemas.media_schema import (
    MediaCreateSchema,
    MediaUpdateSchema,
    MediaResponseSchema,
    MediaListResponseSchema,
    MediaMessageResponseSchema,
    VideoDetailResponseSchema,
    VideoListItemSchema,
    VideoListResponseSchema
)

# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def normalize_static_url(url: str | None) -> str | None:
    if url is None:
        return None

    parsed_url = urlparse(url)

    if parsed_url.path.startswith("/static/"):
        public_base_url = os.getenv("PUBLIC_BASE_URL")

        if public_base_url:
            return public_base_url.rstrip("/") + parsed_url.path

    return url


def to_media_response(media) -> MediaResponseSchema:
    return MediaResponseSchema(
        id_media=media.id_media,
        id_usuario=media.id_usuario,
        titulo=media.titulo,
        descripcion=media.descripcion,
        url_archivo=normalize_static_url(media.url_archivo),
        url_miniatura=normalize_static_url(media.url_miniatura),
        mime_type=media.mime_type
    )

# --------------------------------------------------
# OBTENER TODOS LOS MEDIA
# --------------------------------------------------

def get_all_media(db: Session) -> MediaListResponseSchema:
    media_list = media_repository.get_all(db)

    return MediaListResponseSchema(
        media=[to_media_response(media) for media in media_list]
    )


# --------------------------------------------------
# OBTENER UN MEDIA POR ID
# --------------------------------------------------

def get_media_by_id(db: Session, media_id: int) -> MediaResponseSchema:
    media = media_repository.get_by_id(db, media_id)

    if media is None:
        raise HTTPException(status_code=404, detail="Media no encontrado")

    return to_media_response(media)


def get_all_videos(db: Session) -> VideoListResponseSchema:
    videos = media_repository.get_videos(db)

    return VideoListResponseSchema(
        videos=[
            VideoListItemSchema(
                id_media=video.id_media,
                titulo=video.titulo,
                descripcion=video.descripcion,
                url_miniatura=normalize_static_url(video.url_miniatura)
            )
            for video in videos
        ]
    )


def get_video_by_id(db: Session, media_id: int) -> VideoDetailResponseSchema:
    video = media_repository.get_by_id(db, media_id)

    if video is None or video.mime_type is None or not video.mime_type.startswith("video/"):
        raise HTTPException(status_code=404, detail="Video no encontrado")

    return VideoDetailResponseSchema(
        id_media=video.id_media,
        titulo=video.titulo,
        descripcion=video.descripcion,
        url_archivo=normalize_static_url(video.url_archivo),
        mime_type=video.mime_type
    )

# --------------------------------------------------
# CREAR UN MEDIA
# --------------------------------------------------

def create_media(db: Session, media_data: MediaCreateSchema) -> MediaMessageResponseSchema:
    usuario = usuario_repository.get_by_id(db, media_data.id_usuario)

    if usuario is None:
        raise HTTPException(status_code=404, detail="El usuario asociado no existe")

    new_media_data = {
        "id_usuario": media_data.id_usuario,
        "titulo": media_data.titulo,
        "descripcion": media_data.descripcion,
        "url_archivo": media_data.url_archivo,
        "url_miniatura": media_data.url_miniatura,
        "mime_type": media_data.mime_type
    }

    new_media = media_repository.create(db, new_media_data)

    return MediaMessageResponseSchema(
        message="Media creado correctamente",
        media=to_media_response(new_media)
    )

# --------------------------------------------------
# ACTUALIZAR UN MEDIA
# --------------------------------------------------

def update_media(
    db: Session,
    media_id: int,
    media_data: MediaUpdateSchema
) -> MediaMessageResponseSchema:
    updated_fields = {}

    if media_data.id_usuario is not None:
        usuario = usuario_repository.get_by_id(db, media_data.id_usuario)
        if usuario is None:
            raise HTTPException(status_code=404, detail="El usuario asociado no existe")
        updated_fields["id_usuario"] = media_data.id_usuario

    if media_data.titulo is not None:
        updated_fields["titulo"] = media_data.titulo

    if media_data.descripcion is not None:
        updated_fields["descripcion"] = media_data.descripcion

    if media_data.url_archivo is not None:
        updated_fields["url_archivo"] = media_data.url_archivo

    if media_data.url_miniatura is not None:
        updated_fields["url_miniatura"] = media_data.url_miniatura

    if media_data.mime_type is not None:
        updated_fields["mime_type"] = media_data.mime_type

    updated_media = media_repository.update(db, media_id, updated_fields)

    if updated_media is None:
        raise HTTPException(status_code=404, detail="Media no encontrado")

    return MediaMessageResponseSchema(
        message="Media actualizado correctamente",
        media=to_media_response(updated_media)
    )


# --------------------------------------------------
# BORRAR UN MEDIA
# --------------------------------------------------

def delete_media(db: Session, media_id: int) -> MediaMessageResponseSchema:
    deleted_media = media_repository.delete(db, media_id)

    if deleted_media is None:
        raise HTTPException(status_code=404, detail="Media no encontrado")

    return MediaMessageResponseSchema(
        message="Media eliminado correctamente",
        media=to_media_response(deleted_media)
    )
