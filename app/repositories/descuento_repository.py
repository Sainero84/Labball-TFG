from sqlalchemy.orm import Session
from app.entities.descuento_entity import DescuentoEntity


# --------------------------------------------------
# OBTENER TODOS LOS DESCUENTOS
# --------------------------------------------------

def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(DescuentoEntity).all()


# --------------------------------------------------
# OBTENER UN DESCUENTO POR ID
# --------------------------------------------------

def get_by_id(db: Session, descuento_id: int):
    """Realiza la operacion get by id contra la base de datos."""
    return db.query(DescuentoEntity).filter(
        DescuentoEntity.id_descuento == descuento_id
    ).first()


# --------------------------------------------------
# OBTENER UN DESCUENTO POR CÓDIGO
# --------------------------------------------------

def get_by_codigo(db: Session, codigo: str):
    """Realiza la operacion get by codigo contra la base de datos."""
    return db.query(DescuentoEntity).filter(
        DescuentoEntity.codigo == codigo
    ).first()


# --------------------------------------------------
# CREAR UN DESCUENTO
# --------------------------------------------------

def create(db: Session, descuento_data: dict):
    """Realiza la operacion create contra la base de datos."""
    new_descuento = DescuentoEntity(
        codigo=descuento_data["codigo"],
        porcentaje=descuento_data["porcentaje"]
    )

    db.add(new_descuento)
    db.commit()
    db.refresh(new_descuento)

    return new_descuento


# --------------------------------------------------
# ACTUALIZAR UN DESCUENTO
# --------------------------------------------------

def update(db: Session, descuento_id: int, updated_fields: dict):
    """Realiza la operacion update contra la base de datos."""
    descuento = get_by_id(db, descuento_id)

    if descuento is None:
        return None

    for key, value in updated_fields.items():
        setattr(descuento, key, value)

    db.commit()
    db.refresh(descuento)

    return descuento


# --------------------------------------------------
# BORRAR UN DESCUENTO
# --------------------------------------------------

def delete(db: Session, descuento_id: int):
    """Realiza la operacion delete contra la base de datos."""
    descuento = get_by_id(db, descuento_id)

    if descuento is None:
        return None

    db.delete(descuento)
    db.commit()

    return descuento