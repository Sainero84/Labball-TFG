from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import get_current_admin_user
from app.schemas.descuento_schema import (
    DescuentoCreateSchema,
    DescuentoUpdateSchema,
    DescuentoResponseSchema,
    DescuentoListResponseSchema,
    DescuentoMessageResponseSchema
)
from app.services.descuento_service import (
    get_all_descuentos,
    get_descuento_by_id,
    create_descuento,
    update_descuento,
    delete_descuento
)

router = APIRouter(prefix="/descuentos", tags=["Descuentos"])


@router.get("/", response_model=DescuentoListResponseSchema)
def get_descuentos(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_descuentos(db)


@router.get("/{descuento_id}", response_model=DescuentoResponseSchema)
def get_descuento(
    descuento_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_descuento_by_id(db, descuento_id)


@router.post("/", response_model=DescuentoMessageResponseSchema)
def create_new_descuento(
    descuento_data: DescuentoCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return create_descuento(db, descuento_data)


@router.put("/{descuento_id}", response_model=DescuentoMessageResponseSchema)
def update_existing_descuento(
    descuento_id: int,
    descuento_data: DescuentoUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_descuento(db, descuento_id, descuento_data)


@router.delete("/{descuento_id}", response_model=DescuentoMessageResponseSchema)
def delete_existing_descuento(
    descuento_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return delete_descuento(db, descuento_id)
