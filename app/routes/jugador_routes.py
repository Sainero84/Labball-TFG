from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.schemas.jugador_schema import (
    JugadorResponseSchema
)
from app.services.jugador_service import (
    get_jugador_by_user
)
from app.firebase.firebase_dependencies import get_current_user

router = APIRouter(prefix="/jugadores", tags=["Jugadores"])


@router.get("/me", response_model=JugadorResponseSchema)
def get_jugador_me(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Expone la ruta encargada de get jugador me y delega la logica principal."""
    return get_jugador_by_user(db, current_user)

