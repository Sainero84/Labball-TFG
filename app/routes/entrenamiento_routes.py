from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db

from app.schemas.entrenamiento_schema import (
    EntrenamientoResponseSchema,
    EntrenamientoListResponseSchema
)

from app.services import entrenamiento_service
from app.firebase.firebase_dependencies import get_current_user


router = APIRouter(
    prefix="/entrenamientos",
    tags=["Entrenamientos"]
)


@router.get(
    "/me",
    response_model=EntrenamientoListResponseSchema
)
def get_entrenamientos_me(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):

    """Expone la ruta encargada de get entrenamientos me y delega la logica principal."""
    return entrenamiento_service.get_entrenamientos_by_user(
        db,
        current_user
    )


@router.get(
    "/me/{entrenamiento_id}",
    response_model=EntrenamientoResponseSchema
)
def get_entrenamiento_me_by_id(
    entrenamiento_id: int,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):

    """Expone la ruta encargada de get entrenamiento me by id y delega la logica principal."""
    return entrenamiento_service.get_entrenamiento_by_user(
        db,
        entrenamiento_id,
        current_user
    )

