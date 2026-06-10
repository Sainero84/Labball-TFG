from sqlalchemy.orm import Session

from app.entities.entrenador_entity import EntrenadorEntity


def get_all(db: Session):
    return db.query(EntrenadorEntity).order_by(EntrenadorEntity.nombre.asc()).all()
