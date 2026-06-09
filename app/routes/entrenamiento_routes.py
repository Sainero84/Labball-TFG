from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db

from app.schemas.entrenamiento_schema import (
    EntrenamientoCreateSchema,
    EntrenamientoUpdateSchema,
    EntrenamientoResponseSchema,
    EntrenamientoListResponseSchema,
    EntrenamientoMessageResponseSchema
)

from app.services import entrenamiento_service
from app.firebase.firebase_dependencies import get_current_admin_user, get_current_user


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

    return entrenamiento_service.get_entrenamiento_by_user(
        db,
        entrenamiento_id,
        current_user
    )


# --------------------------------------------------
# OBTENER TODOS LOS ENTRENAMIENTOS
# --------------------------------------------------

@router.get(
    "/",
    response_model=EntrenamientoListResponseSchema
)
def get_all_entrenamientos(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):

    return entrenamiento_service.get_all_entrenamientos(db)


# --------------------------------------------------
# OBTENER ENTRENAMIENTO POR ID
# --------------------------------------------------

@router.get(
    "/{entrenamiento_id}",
    response_model=EntrenamientoResponseSchema
)
def get_entrenamiento_by_id(
    entrenamiento_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):

    return entrenamiento_service.get_entrenamiento_by_id(
        db,
        entrenamiento_id
    )


# --------------------------------------------------
# CREAR ENTRENAMIENTO
# --------------------------------------------------

@router.post(
    "/",
    response_model=EntrenamientoMessageResponseSchema
)
def create_entrenamiento(
    entrenamiento_data: EntrenamientoCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):

    return entrenamiento_service.create_entrenamiento(
        db,
        entrenamiento_data
    )


# --------------------------------------------------
# ACTUALIZAR ENTRENAMIENTO
# --------------------------------------------------

@router.put(
    "/{entrenamiento_id}",
    response_model=EntrenamientoMessageResponseSchema
)
def update_entrenamiento(
    entrenamiento_id: int,
    entrenamiento_data: EntrenamientoUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):

    return entrenamiento_service.update_entrenamiento(
        db,
        entrenamiento_id,
        entrenamiento_data
    )


# --------------------------------------------------
# BORRAR ENTRENAMIENTO
# --------------------------------------------------

@router.delete(
    "/{entrenamiento_id}",
    response_model=EntrenamientoMessageResponseSchema
)
def delete_entrenamiento(
    entrenamiento_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):

    return entrenamiento_service.delete_entrenamiento(
        db,
        entrenamiento_id
    )
