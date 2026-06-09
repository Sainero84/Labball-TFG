from sqlalchemy.orm import Session
from app.entities.media_entity import MediaEntity


# --------------------------------------------------
# OBTENER TODOS LOS MEDIA
# --------------------------------------------------

def get_all(db: Session):
    return db.query(MediaEntity).all()


# --------------------------------------------------
# OBTENER UN MEDIA POR ID
# --------------------------------------------------

def get_by_id(db: Session, media_id: int):
    return db.query(MediaEntity).filter(MediaEntity.id_media == media_id).first()


def get_videos(db: Session):
    return db.query(MediaEntity).filter(
        MediaEntity.mime_type.like("video/%")
    ).all()


# --------------------------------------------------
# CREAR UN MEDIA
# --------------------------------------------------

def create(db: Session, media_data: dict):
    new_media = MediaEntity(
        id_usuario=media_data["id_usuario"],
        titulo=media_data["titulo"],
        descripcion=media_data["descripcion"],
        url_archivo=media_data["url_archivo"],
        url_miniatura=media_data["url_miniatura"],
        mime_type=media_data["mime_type"]
    )

    db.add(new_media)
    db.commit()
    db.refresh(new_media)

    return new_media


# --------------------------------------------------
# ACTUALIZAR UN MEDIA
# --------------------------------------------------

def update(db: Session, media_id: int, updated_fields: dict):
    media = get_by_id(db, media_id)

    if media is None:
        return None

    for key, value in updated_fields.items():
        setattr(media, key, value)

    db.commit()
    db.refresh(media)

    return media


# --------------------------------------------------
# BORRAR UN MEDIA
# --------------------------------------------------

def delete(db: Session, media_id: int):
    media = get_by_id(db, media_id)

    if media is None:
        return None

    db.delete(media)
    db.commit()

    return media
