from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.schemas.jugador_schema import (
    JugadorCreateSchema,
    JugadorUpdateSchema,
    JugadorResponseSchema,
    JugadorListResponseSchema,
    JugadorMessageResponseSchema
)
from app.services.jugador_service import (
    get_all_jugadores,
    get_jugador_by_id,
    get_jugador_by_user,
    create_jugador,
    update_jugador,
    delete_jugador
)
from app.firebase.firebase_dependencies import get_current_admin_user, get_current_user

router = APIRouter(prefix="/jugadores", tags=["Jugadores"])


@router.get("/me", response_model=JugadorResponseSchema)
def get_jugador_me(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return get_jugador_by_user(db, current_user)


@router.get("/", response_model=JugadorListResponseSchema)
def get_jugadores(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_jugadores(db)


@router.get("/{jugador_id}", response_model=JugadorResponseSchema)
def get_jugador(
    jugador_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_jugador_by_id(db, jugador_id)


@router.post("/", response_model=JugadorMessageResponseSchema)
def create_new_jugador(
    jugador_data: JugadorCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_jugador(db, jugador_data)


@router.put("/{jugador_id}", response_model=JugadorMessageResponseSchema)
def update_existing_jugador(
    jugador_id: int,
    jugador_data: JugadorUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_jugador(db, jugador_id, jugador_data)


@router.delete("/{jugador_id}", response_model=JugadorMessageResponseSchema)
def delete_existing_jugador(
    jugador_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_jugador(db, jugador_id)
