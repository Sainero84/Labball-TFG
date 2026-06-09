from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_admin_user, get_current_user
from app.schemas.media_schema import (
    MediaCreateSchema,
    MediaUpdateSchema,
    MediaResponseSchema,
    MediaListResponseSchema,
    MediaMessageResponseSchema,
    VideoDetailResponseSchema,
    VideoListResponseSchema
)
from app.services.media_service import (
    get_all_media,
    get_all_videos,
    get_media_by_id,
    get_video_by_id,
    create_media,
    update_media,
    delete_media
)

router = APIRouter(prefix="/media", tags=["Media"])


@router.get("/videos", response_model=VideoListResponseSchema)
def get_videos(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return get_all_videos(db)


@router.get("/videos/{media_id}", response_model=VideoDetailResponseSchema)
def get_video(
    media_id: int,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return get_video_by_id(db, media_id)


@router.get("/", response_model=MediaListResponseSchema)
def get_media_list(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_media(db)


@router.get("/{media_id}", response_model=MediaResponseSchema)
def get_media_item(
    media_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_media_by_id(db, media_id)


@router.post("/", response_model=MediaMessageResponseSchema)
def create_new_media(
    media_data: MediaCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_media(db, media_data)


@router.put("/{media_id}", response_model=MediaMessageResponseSchema)
def update_existing_media(
    media_id: int,
    media_data: MediaUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_media(db, media_id, media_data)


@router.delete("/{media_id}", response_model=MediaMessageResponseSchema)
def delete_existing_media(
    media_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_media(db, media_id)
