from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_user
from app.schemas.inscripcion_schema import (
    ReservaCreateSchema,
    ReservaListResponseSchema,
    ReservaMessageResponseSchema,
    ReservaPreviewRequestSchema,
    ReservaPreviewResponseSchema,
    ReservaResponseSchema
)
from app.services.inscripcion_service import (
    create_reserva,
    get_reserva_by_user,
    get_reservas_by_user,
    preview_reserva_precio
)


router = APIRouter(prefix="/reservas", tags=["Reservas"])


@router.post("/preview", response_model=ReservaPreviewResponseSchema)
def preview_reserva(
    preview_data: ReservaPreviewRequestSchema,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return preview_reserva_precio(db, preview_data)


@router.post("/", response_model=ReservaMessageResponseSchema)
def create_new_reserva(
    reserva_data: ReservaCreateSchema,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return create_reserva(db, reserva_data, current_user)


@router.get("/me", response_model=ReservaListResponseSchema)
def get_reservas_me(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return get_reservas_by_user(db, current_user)


@router.get("/me/{reserva_id}", response_model=ReservaResponseSchema)
def get_reserva_me_by_id(
    reserva_id: int,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return get_reserva_by_user(db, reserva_id, current_user)
