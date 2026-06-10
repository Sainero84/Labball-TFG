# Importamos Session para trabajar con la base de datos
from sqlalchemy import or_
from sqlalchemy.orm import Session, joinedload

# Importamos la entidad Usuario
from app.entities.entrenamiento_entity import EntrenamientoEntity
from app.entities.inscripcion_entity import InscripcionEntity
from app.entities.jugador_entity import JugadorEntity
from app.entities.media_entity import MediaEntity
from app.entities.usuario_entity import UsuarioEntity


# --------------------------------------------------
# OBTENER TODOS LOS USUARIOS
# --------------------------------------------------

def get_all(db: Session):
    return db.query(UsuarioEntity).options(
        joinedload(UsuarioEntity.jugador)
    ).all()


def get_all_entrenadores(db: Session):
    return db.query(UsuarioEntity).options(
        joinedload(UsuarioEntity.jugador)
    ).filter(
        UsuarioEntity.es_admin.is_(True),
        UsuarioEntity.es_entrenador.is_(True)
    ).order_by(UsuarioEntity.correo.asc()).all()


def count_admins(db: Session) -> int:
    return db.query(UsuarioEntity).filter(UsuarioEntity.es_admin.is_(True)).count()


# --------------------------------------------------
# OBTENER UN USUARIO POR ID
# --------------------------------------------------

def get_by_id(db: Session, user_id: int):
    return db.query(UsuarioEntity).options(
        joinedload(UsuarioEntity.jugador)
    ).filter(UsuarioEntity.id_usuario == user_id).first()


# --------------------------------------------------
# OBTENER UN USUARIO POR CORREO
# --------------------------------------------------

def get_by_correo(db: Session, correo: str):
    return db.query(UsuarioEntity).filter(UsuarioEntity.correo == correo).first()


# --------------------------------------------------
# CREAR UN USUARIO
# --------------------------------------------------

def create(db: Session, user_data: dict):
    new_user = UsuarioEntity(
        firebase_uid=user_data["firebase_uid"],
        correo=user_data["correo"],
        es_admin=user_data["es_admin"],
        es_super_admin=user_data.get("es_super_admin", False),
        es_entrenador=user_data.get("es_entrenador", False),
        id_codigo_administrador=user_data["id_codigo_administrador"],
        telefono=user_data["telefono"],
        nombre=user_data.get("nombre"),
        apellido_1=user_data.get("apellido_1"),
        fecha_nacimiento=user_data.get("fecha_nacimiento"),
        foto_perfil_url=user_data["foto_perfil_url"],
        foto_perfil_mime_type=user_data["foto_perfil_mime_type"]
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return new_user


def create_without_commit(db: Session, user_data: dict):
    new_user = UsuarioEntity(
        firebase_uid=user_data["firebase_uid"],
        correo=user_data["correo"],
        es_admin=user_data["es_admin"],
        es_super_admin=user_data.get("es_super_admin", False),
        es_entrenador=user_data.get("es_entrenador", False),
        id_codigo_administrador=user_data["id_codigo_administrador"],
        telefono=user_data["telefono"],
        nombre=user_data.get("nombre"),
        apellido_1=user_data.get("apellido_1"),
        fecha_nacimiento=user_data.get("fecha_nacimiento"),
        foto_perfil_url=user_data["foto_perfil_url"],
        foto_perfil_mime_type=user_data["foto_perfil_mime_type"]
    )

    db.add(new_user)

    return new_user

# --------------------------------------------------
# ACTUALIZAR UN USUARIO
# --------------------------------------------------

def update(db: Session, user_id: int, updated_fields: dict):
    user = get_by_id(db, user_id)

    if user is None:
        return None

    for key, value in updated_fields.items():
        setattr(user, key, value)

    db.commit()
    db.refresh(user)

    return user


# --------------------------------------------------
# BORRAR UN USUARIO
# --------------------------------------------------

def delete(db: Session, user_id: int):
    user = get_by_id(db, user_id)

    if user is None:
        return None

    db.delete(user)
    db.commit()

    return user


def delete_without_commit(db: Session, user: UsuarioEntity):
    db.delete(user)


def get_related_data_counts(db: Session, user_id: int) -> dict[str, int]:
    entrenamientos_count = db.query(EntrenamientoEntity).outerjoin(
        InscripcionEntity,
        EntrenamientoEntity.id_inscripcion == InscripcionEntity.id_inscripcion
    ).filter(
        or_(
            EntrenamientoEntity.id_usuario == user_id,
            InscripcionEntity.id_usuario == user_id
        )
    ).count()

    return {
        "reservas": db.query(InscripcionEntity).filter(
            InscripcionEntity.id_usuario == user_id
        ).count(),
        "entrenamientos": entrenamientos_count,
        "jugadores": db.query(JugadorEntity).filter(
            JugadorEntity.id_usuario == user_id
        ).count(),
        "media": db.query(MediaEntity).filter(
            MediaEntity.id_usuario == user_id
        ).count()
    }

def get_by_firebase_uid(db: Session, firebase_uid: str):
    return db.query(UsuarioEntity).filter(
        UsuarioEntity.firebase_uid == firebase_uid
    ).first()


def get_by_codigo_administrador_id(db: Session, codigo_administrador_id: int):
    return db.query(UsuarioEntity).filter(
        UsuarioEntity.id_codigo_administrador == codigo_administrador_id
    ).first()
