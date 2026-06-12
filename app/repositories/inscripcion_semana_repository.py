from sqlalchemy.orm import Session

from app.entities.inscripcion_semana_entity import InscripcionSemanaEntity


# --------------------------------------------------
# OBTENER TODAS LAS RELACIONES
# --------------------------------------------------

def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(InscripcionSemanaEntity).all()


# --------------------------------------------------
# OBTENER UNA RELACIÓN CONCRETA
# --------------------------------------------------

def get_by_ids(db: Session, id_inscripcion: int, id_semana: int):
    """Realiza la operacion get by ids contra la base de datos."""
    return db.query(InscripcionSemanaEntity).filter(
        InscripcionSemanaEntity.id_inscripcion == id_inscripcion,
        InscripcionSemanaEntity.id_semana == id_semana
    ).first()


# --------------------------------------------------
# OBTENER TODAS LAS SEMANAS DE UNA INSCRIPCIÓN
# --------------------------------------------------

def get_by_inscripcion_id(db: Session, id_inscripcion: int):
    """Realiza la operacion get by inscripcion id contra la base de datos."""
    return db.query(InscripcionSemanaEntity).filter(
        InscripcionSemanaEntity.id_inscripcion == id_inscripcion
    ).all()


# --------------------------------------------------
# OBTENER TODAS LAS INSCRIPCIONES DE UNA SEMANA
# --------------------------------------------------

def get_by_semana_id(db: Session, id_semana: int):
    """Realiza la operacion get by semana id contra la base de datos."""
    return db.query(InscripcionSemanaEntity).filter(
        InscripcionSemanaEntity.id_semana == id_semana
    ).all()


# --------------------------------------------------
# CREAR UNA RELACIÓN
# --------------------------------------------------

def create(db: Session, relation_data: dict):
    """Realiza la operacion create contra la base de datos."""
    new_relation = InscripcionSemanaEntity(
        id_inscripcion=relation_data["id_inscripcion"],
        id_semana=relation_data["id_semana"]
    )

    db.add(new_relation)
    db.commit()
    db.refresh(new_relation)

    return new_relation


# --------------------------------------------------
# BORRAR UNA RELACIÓN
# --------------------------------------------------

def delete(db: Session, id_inscripcion: int, id_semana: int):
    """Realiza la operacion delete contra la base de datos."""
    relation = get_by_ids(db, id_inscripcion, id_semana)

    if relation is None:
        return None

    db.delete(relation)
    db.commit()

    return relation