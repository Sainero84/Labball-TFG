from sqlalchemy.orm import Session
from app.entities.semana_entity import SemanaEntity


# --------------------------------------------------
# OBTENER TODAS LAS SEMANAS
# --------------------------------------------------

def get_all(db: Session):
    return db.query(SemanaEntity).all()


# --------------------------------------------------
# OBTENER UNA SEMANA POR ID
# --------------------------------------------------

def get_by_id(db: Session, semana_id: int):
    return db.query(SemanaEntity).filter(SemanaEntity.id_semana == semana_id).first()


# --------------------------------------------------
# CREAR UNA SEMANA
# --------------------------------------------------

def create(db: Session, semana_data: dict):
    new_semana = SemanaEntity(
        semana=semana_data["semana"],
        horario=semana_data["horario"]
    )

    db.add(new_semana)
    db.commit()
    db.refresh(new_semana)

    return new_semana


# --------------------------------------------------
# ACTUALIZAR UNA SEMANA
# --------------------------------------------------

def update(db: Session, semana_id: int, updated_fields: dict):
    semana = get_by_id(db, semana_id)

    if semana is None:
        return None

    for key, value in updated_fields.items():
        setattr(semana, key, value)

    db.commit()
    db.refresh(semana)

    return semana


# --------------------------------------------------
# BORRAR UNA SEMANA
# --------------------------------------------------

def delete(db: Session, semana_id: int):
    semana = get_by_id(db, semana_id)

    if semana is None:
        return None

    db.delete(semana)
    db.commit()

    return semana