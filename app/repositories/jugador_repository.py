from sqlalchemy.orm import Session, joinedload
from app.entities.jugador_entity import JugadorEntity


# --------------------------------------------------
# OBTENER TODOS LOS JUGADORES
# --------------------------------------------------

def get_all(db: Session):
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).all()


# --------------------------------------------------
# OBTENER UN JUGADOR POR ID
# --------------------------------------------------

def get_by_id(db: Session, jugador_id: int):
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).filter(
        JugadorEntity.id_jugador == jugador_id
    ).first()


# --------------------------------------------------
# OBTENER UN JUGADOR POR USUARIO
# --------------------------------------------------

def get_by_usuario_id(db: Session, usuario_id: int):
    return db.query(JugadorEntity).options(
        joinedload(JugadorEntity.usuario)
    ).filter(
        JugadorEntity.id_usuario == usuario_id
    ).first()


# --------------------------------------------------
# CREAR UN JUGADOR
# --------------------------------------------------

def create(db: Session, jugador_data: dict):
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
    jugador = get_by_id(db, jugador_id)

    if jugador is None:
        return None

    db.delete(jugador)
    db.commit()

    return jugador
