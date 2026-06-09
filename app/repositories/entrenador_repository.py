from sqlalchemy.orm import Session

from app.entities.entrenador_entity import EntrenadorEntity


def get_all(db: Session):
    return db.query(EntrenadorEntity).order_by(EntrenadorEntity.nombre.asc()).all()


def get_by_nombre(db: Session, nombre: str):
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.nombre == nombre
    ).first()


def create(db: Session, entrenador_data: dict):
    entrenador = EntrenadorEntity(**entrenador_data)

    db.add(entrenador)
    db.commit()
    db.refresh(entrenador)

    return entrenador
