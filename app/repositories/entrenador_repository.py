from sqlalchemy.orm import Session

from app.entities.entrenador_entity import EntrenadorEntity


def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(EntrenadorEntity).order_by(EntrenadorEntity.nombre.asc()).all()


def get_by_id(db: Session, entrenador_id: int):
    """Realiza la operacion get by id contra la base de datos."""
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.id_entrenador == entrenador_id
    ).first()


def get_by_nombre(db: Session, nombre: str):
    """Realiza la operacion get by nombre contra la base de datos."""
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.nombre == nombre
    ).first()


def get_by_usuario_id(db: Session, usuario_id: int):
    """Realiza la operacion get by usuario id contra la base de datos."""
    return db.query(EntrenadorEntity).filter(
        EntrenadorEntity.id_usuario == usuario_id
    ).first()


def create_without_commit(db: Session, entrenador_data: dict):
    """Realiza la operacion create without commit contra la base de datos."""
    entrenador = EntrenadorEntity(**entrenador_data)

    db.add(entrenador)

    return entrenador
