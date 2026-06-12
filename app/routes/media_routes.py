from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_user
from app.schemas.media_schema import (
    VideoDetailResponseSchema,
    VideoListResponseSchema
)
from app.services.media_service import (
    get_all_videos,
    get_video_by_id
)

router = APIRouter(prefix="/media", tags=["Media"])


@router.get("/videos", response_model=VideoListResponseSchema)
def get_videos(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Expone la ruta encargada de get videos y delega la logica principal."""
    return get_all_videos(db)


@router.get("/videos/{media_id}", response_model=VideoDetailResponseSchema)
def get_video(
    media_id: int,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Expone la ruta encargada de get video y delega la logica principal."""
    return get_video_by_id(db, media_id)

