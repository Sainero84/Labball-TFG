from sqlalchemy.orm import Session

from app.entities.ubicacion_entity import UbicacionEntity


def get_all(db: Session):
    return db.query(UbicacionEntity).order_by(UbicacionEntity.nombre.asc()).all()


def get_by_nombre(db: Session, nombre: str):
    return db.query(UbicacionEntity).filter(
        UbicacionEntity.nombre == nombre
    ).first()


def create(db: Session, ubicacion_data: dict):
    ubicacion = UbicacionEntity(**ubicacion_data)

    db.add(ubicacion)
    db.commit()
    db.refresh(ubicacion)

    return ubicacion


def get_by_id(db: Session, ubicacion_id: int):
    return db.query(UbicacionEntity).filter(
        UbicacionEntity.id_ubicacion == ubicacion_id
    ).first()


def delete(db: Session, ubicacion_id: int):
    ubicacion = get_by_id(db, ubicacion_id)

    if ubicacion is None:
        return None

    db.delete(ubicacion)
    db.commit()

    return ubicacion
