from sqlalchemy import or_
from sqlalchemy.orm import Session

from app.entities.entrenamiento_entity import EntrenamientoEntity
from app.entities.inscripcion_entity import InscripcionEntity


# --------------------------------------------------
# OBTENER TODOS LOS ENTRENAMIENTOS
# --------------------------------------------------

def get_all(db: Session):

    return db.query(EntrenamientoEntity).all()


# --------------------------------------------------
# OBTENER ENTRENAMIENTO POR ID
# --------------------------------------------------

def get_by_id(db: Session, entrenamiento_id: int):

    return db.query(EntrenamientoEntity).filter(
        EntrenamientoEntity.id_entrenamiento == entrenamiento_id
    ).first()


def get_by_usuario_id(db: Session, usuario_id: int):

    return db.query(EntrenamientoEntity).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        or_(
            EntrenamientoEntity.id_usuario == usuario_id,
            InscripcionEntity.id_usuario == usuario_id
        )
    ).all()


def exists_by_usuario_id(db: Session, usuario_id: int) -> bool:

    return db.query(EntrenamientoEntity.id_entrenamiento).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        or_(
            EntrenamientoEntity.id_usuario == usuario_id,
            InscripcionEntity.id_usuario == usuario_id
        )
    ).first() is not None


def count_by_usuario_id(db: Session, usuario_id: int) -> int:

    return db.query(EntrenamientoEntity).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        or_(
            EntrenamientoEntity.id_usuario == usuario_id,
            InscripcionEntity.id_usuario == usuario_id
        )
    ).count()


def get_by_reserva_id(db: Session, reserva_id: int):

    return db.query(EntrenamientoEntity).filter(
        or_(
            EntrenamientoEntity.id_inscripcion == reserva_id,
            EntrenamientoEntity.id_reserva == reserva_id
        )
    ).all()


def count_by_reserva_id(db: Session, reserva_id: int) -> int:

    return db.query(EntrenamientoEntity).filter(
        or_(
            EntrenamientoEntity.id_inscripcion == reserva_id,
            EntrenamientoEntity.id_reserva == reserva_id
        )
    ).count()


def get_by_id_and_usuario_id(db: Session, entrenamiento_id: int, usuario_id: int):

    return db.query(EntrenamientoEntity).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        EntrenamientoEntity.id_entrenamiento == entrenamiento_id,
        or_(
            EntrenamientoEntity.id_usuario == usuario_id,
            InscripcionEntity.id_usuario == usuario_id
        )
    ).first()


# --------------------------------------------------
# CREAR ENTRENAMIENTO
# --------------------------------------------------

def create(db: Session, entrenamiento_data: dict):

    new_entrenamiento = EntrenamientoEntity(**entrenamiento_data)

    db.add(new_entrenamiento)

    db.commit()

    db.refresh(new_entrenamiento)

    return new_entrenamiento


def create_without_commit(db: Session, entrenamiento_data: dict):

    new_entrenamiento = EntrenamientoEntity(**entrenamiento_data)
    db.add(new_entrenamiento)

    return new_entrenamiento


def delete_by_reserva_id_without_commit(db: Session, reserva_id: int):

    entrenamientos = get_by_reserva_id(db, reserva_id)

    for entrenamiento in entrenamientos:
        db.delete(entrenamiento)

    return entrenamientos


# --------------------------------------------------
# ACTUALIZAR ENTRENAMIENTO
# --------------------------------------------------

def update(
    db: Session,
    entrenamiento_id: int,
    updated_fields: dict
):

    entrenamiento = get_by_id(db, entrenamiento_id)

    if entrenamiento is None:
        return None

    for key, value in updated_fields.items():
        setattr(entrenamiento, key, value)

    db.commit()

    db.refresh(entrenamiento)

    return entrenamiento


# --------------------------------------------------
# BORRAR ENTRENAMIENTO
# --------------------------------------------------

def delete(db: Session, entrenamiento_id: int):

    entrenamiento = get_by_id(db, entrenamiento_id)

    if entrenamiento is None:
        return None

    db.delete(entrenamiento)

    db.commit()

    return entrenamiento
