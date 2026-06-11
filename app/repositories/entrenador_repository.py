from sqlalchemy.orm import Session

from app.entities.entrenador_entity import EntrenadorEntity


def get_all(db: Session):
    return db.query(EntrenadorEntity).order_by(EntrenadorEntity.nombre.asc()).all()


def get_by_id(db: Session, entrenador_id: int):
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.id_entrenador == entrenador_id
    ).first()


def get_by_nombre(db: Session, nombre: str):
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.nombre == nombre
    ).first()


def get_by_usuario_id(db: Session, usuario_id: int):
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.id_usuario == usuario_id
    ).first()


def create_without_commit(db: Session, entrenador_data: dict):
    entrenador = EntrenadorEntity(**entrenador_data)

    db.add(entrenador)

    return entrenador
