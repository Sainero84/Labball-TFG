from sqlalchemy import or_
from sqlalchemy.orm import Session, joinedload

from app.entities.entrenamiento_entity import EntrenamientoEntity
from app.entities.inscripcion_entity import InscripcionEntity
from app.entities.jugador_entity import JugadorEntity


# --------------------------------------------------
# OBTENER TODOS LOS JUGADORES
# --------------------------------------------------

def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).all()


# --------------------------------------------------
# OBTENER UN JUGADOR POR ID
# --------------------------------------------------

def get_by_id(db: Session, jugador_id: int):
    """Realiza la operacion get by id contra la base de datos."""
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).filter(
        JugadorEntity.id_jugador == jugador_id
    ).first()


# --------------------------------------------------
# OBTENER UN JUGADOR POR USUARIO
# --------------------------------------------------

def get_by_usuario_id(db: Session, usuario_id: int):
    """Realiza la operacion get by usuario id contra la base de datos."""
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).filter(
        JugadorEntity.id_usuario == usuario_id
    ).first()


def get_entrenamientos_catalogo_by_jugador(db: Session, jugador: JugadorEntity):
    """Realiza la operacion get entrenamientos catalogo by jugador contra la base de datos."""
    return db.query(EntrenamientoEntity).options(
        joinedload(EntrenamientoEntity.entrenador),
        joinedload(EntrenamientoEntity.ubicacion_catalogo)
    ).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        or_(
            EntrenamientoEntity.id_jugador == jugador.id_jugador,
            InscripcionEntity.id_usuario == jugador.id_usuario
        )
    ).order_by(EntrenamientoEntity.hora_inicio.desc()).all()


# --------------------------------------------------
# CREAR UN JUGADOR
# --------------------------------------------------

def create(db: Session, jugador_data: dict):
    """Realiza la operacion create contra la base de datos."""
    new_jugador = JugadorEntity(
        id_usuario=jugador_data["id_usuario"],
        nombre=jugador_data["nombre"],
        apellidos=jugador_data["apellidos"],
        peso=jugador_data["peso"],
        altura=jugador_data["altura"],
        posicion=jugador_data["posicion"],
        tiro=jugador_data["tiro"],
        fisico=jugador_data["fisico"],
        bote=jugador_data["bote"],
        pase=jugador_data["pase"],
        defensa=jugador_data["defensa"],
        velocidad=jugador_data["velocidad"]
    )

    db.add(new_jugador)
    db.commit()
    db.refresh(new_jugador)

    return new_jugador


def create_without_commit(db: Session, jugador_data: dict):
    """Realiza la operacion create without commit contra la base de datos."""
    new_jugador = JugadorEntity(
        id_usuario=jugador_data["id_usuario"],
        nombre=jugador_data["nombre"],
        apellidos=jugador_data["apellidos"],
        peso=jugador_data["peso"],
        altura=jugador_data["altura"],
        posicion=jugador_data["posicion"],
        tiro=jugador_data["tiro"],
        fisico=jugador_data["fisico"],
        bote=jugador_data["bote"],
        pase=jugador_data["pase"],
        defensa=jugador_data["defensa"],
        velocidad=jugador_data["velocidad"]
    )

    db.add(new_jugador)

    return new_jugador


# --------------------------------------------------
# ACTUALIZAR UN JUGADOR
# --------------------------------------------------

def update(db: Session, jugador_id: int, updated_fields: dict):
    """Realiza la operacion update contra la base de datos."""
    jugador = get_by_id(db, jugador_id)

    if jugador is None:
        return None

    for key, value in updated_fields.items():
        setattr(jugador, key, value)

    db.commit()
    db.refresh(jugador)

    return jugador


# --------------------------------------------------
# BORRAR UN JUGADOR
# --------------------------------------------------

def delete(db: Session, jugador_id: int):
    """Realiza la operacion delete contra la base de datos."""
    jugador = get_by_id(db, jugador_id)

    if jugador is None:
        return None

    db.delete(jugador)
    db.commit()

    return jugador
