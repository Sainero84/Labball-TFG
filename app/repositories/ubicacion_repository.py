from sqlalchemy.orm import Session

from app.entities.ubicacion_entity import UbicacionEntity


def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(UbicacionEntity).order_by(UbicacionEntity.nombre.asc()).all()


def get_by_nombre(db: Session, nombre: str):
    """Realiza la operacion get by nombre contra la base de datos."""
    return db.query(UbicacionEntity).filter(
        UbicacionEntity.nombre == nombre
    ).first()


def create(db: Session, ubicacion_data: dict):
    """Realiza la operacion create contra la base de datos."""
    ubicacion = UbicacionEntity(**ubicacion_data)

    db.add(ubicacion)
    db.commit()
    db.refresh(ubicacion)

    return ubicacion


def get_by_id(db: Session, ubicacion_id: int):
    """Realiza la operacion get by id contra la base de datos."""
    return db.query(UbicacionEntity).filter(
        UbicacionEntity.id_ubicacion == ubicacion_id
    ).first()


def delete(db: Session, ubicacion_id: int):
    """Realiza la operacion delete contra la base de datos."""
    ubicacion = get_by_id(db, ubicacion_id)

    if ubicacion is None:
        return None

    db.delete(ubicacion)
    db.commit()

    return ubicacion
