from sqlalchemy import or_
from sqlalchemy.orm import Session, joinedload

from app.entities.entrenamiento_entity import EntrenamientoEntity
from app.entities.inscripcion_entity import InscripcionEntity
from app.entities.jugador_entity import JugadorEntity


# --------------------------------------------------
# OBTENER TODOS LOS ENTRENAMIENTOS
# --------------------------------------------------

def get_all(db: Session):

    """Realiza la operacion get all contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).all()


# --------------------------------------------------
# OBTENER ENTRENAMIENTO POR ID
# --------------------------------------------------

def get_by_id(db: Session, entrenamiento_id: int):

    """Realiza la operacion get by id contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).filter(
        EntrenamientoEntity.id_entrenamiento == entrenamiento_id
    ).first()


def get_by_usuario_id(db: Session, usuario_id: int):

    """Realiza la operacion get by usuario id contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).outerjoin(
        JugadorEntity,
        EntrenamientoEntity.id_jugador == JugadorEntity.id_jugador
    ).filter(
        or_(
            InscripcionEntity.id_usuario == usuario_id,
            JugadorEntity.id_usuario == usuario_id
        )
    ).all()


def exists_by_usuario_id(db: Session, usuario_id: int) -> bool:

    """Realiza la operacion exists by usuario id contra la base de datos."""
    return db.query(EntrenamientoEntity.id_entrenamiento).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).outerjoin(
        JugadorEntity,
        EntrenamientoEntity.id_jugador == JugadorEntity.id_jugador
    ).filter(
        or_(
            InscripcionEntity.id_usuario == usuario_id,
            JugadorEntity.id_usuario == usuario_id
        )
    ).first() is not None


def count_by_usuario_id(db: Session, usuario_id: int) -> int:

    """Realiza la operacion count by usuario id contra la base de datos."""
    return db.query(EntrenamientoEntity).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).outerjoin(
        JugadorEntity,
        EntrenamientoEntity.id_jugador == JugadorEntity.id_jugador
    ).filter(
        or_(
            InscripcionEntity.id_usuario == usuario_id,
            JugadorEntity.id_usuario == usuario_id
        )
    ).count()


def get_by_reserva_id(db: Session, reserva_id: int):

    """Realiza la operacion get by reserva id contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).filter(
        EntrenamientoEntity.id_inscripcion == reserva_id
    ).all()


def count_by_reserva_id(db: Session, reserva_id: int) -> int:

    """Realiza la operacion count by reserva id contra la base de datos."""
    return db.query(EntrenamientoEntity).filter(
        EntrenamientoEntity.id_inscripcion == reserva_id
    ).count()


def get_by_id_and_usuario_id(db: Session, entrenamiento_id: int, usuario_id: int):

    """Realiza la operacion get by id and usuario id contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).outerjoin(
        JugadorEntity,
        EntrenamientoEntity.id_jugador == JugadorEntity.id_jugador
    ).filter(
        EntrenamientoEntity.id_entrenamiento == entrenamiento_id,
        or_(
            InscripcionEntity.id_usuario == usuario_id,
            JugadorEntity.id_usuario == usuario_id
        )
    ).first()


# --------------------------------------------------
# CREAR ENTRENAMIENTO
# --------------------------------------------------

def create(db: Session, entrenamiento_data: dict):

    """Realiza la operacion create contra la base de datos."""
    new_entrenamiento = EntrenamientoEntity(**entrenamiento_data)

    db.add(new_entrenamiento)

    db.commit()

    db.refresh(new_entrenamiento)

    return new_entrenamiento


def create_without_commit(db: Session, entrenamiento_data: dict):

    """Realiza la operacion create without commit contra la base de datos."""
    new_entrenamiento = EntrenamientoEntity(**entrenamiento_data)
    db.add(new_entrenamiento)

    return new_entrenamiento


def delete_by_reserva_id_without_commit(db: Session, reserva_id: int):

    """Realiza la operacion delete by reserva id without commit contra la base de datos."""
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

    """Realiza la operacion update contra la base de datos."""
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

    """Realiza la operacion delete contra la base de datos."""
    entrenamiento = get_by_id(db, entrenamiento_id)

    if entrenamiento is None:
        return None

    db.delete(entrenamiento)

    db.commit()

    return entrenamiento
