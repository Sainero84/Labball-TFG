from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_user
from app.schemas.tarifa_schema import TarifaListResponseSchema
from app.services.tarifa_service import get_active_tarifas


router = APIRouter(prefix="/tarifas", tags=["Tarifas"])


@router.get("/", response_model=TarifaListResponseSchema)
def get_tarifas(
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """Expone la ruta encargada de get tarifas y delega la logica principal."""
    return get_active_tarifas(db)
