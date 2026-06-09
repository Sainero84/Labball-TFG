# Importamos HTTPException para errores controlados
from fastapi import HTTPException

# Importamos Session
from sqlalchemy.orm import Session

# Importamos el repository
from app.repositories import usuario_repository

# Importamos los schemas
from app.schemas.usuario_schema import (
    UsuarioCreateSchema,
    UsuarioUpdateSchema,
    UsuarioResponseSchema,
    UsuarioListResponseSchema,
    UsuarioMessageResponseSchema
)

# --------------------------------------------------
# FUNCIÓN AUXILIAR
# --------------------------------------------------

def to_usuario_response(user) -> UsuarioResponseSchema:
    return UsuarioResponseSchema(
        id_usuario=user.id_usuario,
        firebase_uid=user.firebase_uid,
        correo=user.correo,
        es_admin=user.es_admin,
        es_super_admin=user.es_super_admin,
        es_entrenador=user.es_entrenador,
        id_codigo_administrador=user.id_codigo_administrador,
        codigo_administrador=(
            user.codigo_administrador_rel.codigo
            if user.codigo_administrador_rel else None
        ),
        telefono=user.telefono,
        nombre=user.nombre,
        apellido_1=user.apellido_1,
        fecha_nacimiento=user.fecha_nacimiento,
        foto_perfil_url=user.foto_perfil_url,
        foto_perfil_mime_type=user.foto_perfil_mime_type
    )

# --------------------------------------------------
# OBTENER TODOS LOS USUARIOS
# --------------------------------------------------

def get_all_usuarios(db: Session) -> UsuarioListResponseSchema:
    usuarios = usuario_repository.get_all(db)

    return UsuarioListResponseSchema(
        usuarios=[to_usuario_response(user) for user in usuarios]
    )


# --------------------------------------------------
# OBTENER UN USUARIO POR ID
# --------------------------------------------------

def get_usuario_by_id(db: Session, user_id: int) -> UsuarioResponseSchema:
    user = usuario_repository.get_by_id(db, user_id)

    if user is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    return to_usuario_response(user)

# --------------------------------------------------
# CREAR UN USUARIO
# --------------------------------------------------

def create_usuario(db: Session, user_data: UsuarioCreateSchema) -> UsuarioMessageResponseSchema:
    existing_uid = usuario_repository.get_by_firebase_uid(db, user_data.firebase_uid)

    if existing_uid is not None:
        raise HTTPException(status_code=400, detail="Ya existe un usuario con ese Firebase UID")

    es_super_admin = user_data.es_super_admin
    es_admin = user_data.es_admin or es_super_admin

    new_user_data = {
        "firebase_uid": user_data.firebase_uid,
        "correo": user_data.correo,
        "es_admin": es_admin,
        "es_super_admin": es_super_admin,
        "es_entrenador": user_data.es_entrenador,
        "id_codigo_administrador": user_data.id_codigo_administrador,
        "telefono": user_data.telefono,
        "nombre": user_data.nombre,
        "apellido_1": user_data.apellido_1,
        "fecha_nacimiento": user_data.fecha_nacimiento,
        "foto_perfil_url": user_data.foto_perfil_url,
        "foto_perfil_mime_type": user_data.foto_perfil_mime_type
    }

    new_user = usuario_repository.create(db, new_user_data)

    return UsuarioMessageResponseSchema(
        message="Usuario creado correctamente",
        usuario=to_usuario_response(new_user)
    )

# --------------------------------------------------
# ACTUALIZAR UN USUARIO
# --------------------------------------------------

def update_usuario(
    db: Session,
    user_id: int,
    user_data: UsuarioUpdateSchema
) -> UsuarioMessageResponseSchema:
    updated_fields = {}

    correo = getattr(user_data, "correo", None)

    if correo is not None:
        existing_user = usuario_repository.get_by_correo(db, correo)
        if existing_user is not None and existing_user.id_usuario != user_id:
            raise HTTPException(status_code=400, detail="Ya existe un usuario con ese correo")
        updated_fields["correo"] = correo


    for field_name in [
        "es_admin",
        "es_super_admin",
        "es_entrenador",
        "id_codigo_administrador",
        "telefono",
        "nombre",
        "apellido_1",
        "fecha_nacimiento",
        "foto_perfil_url",
        "foto_perfil_mime_type"
    ]:
        value = getattr(user_data, field_name, None)

        if value is not None:
            updated_fields[field_name] = value

    if updated_fields.get("es_super_admin") is True:
        updated_fields["es_admin"] = True
    elif updated_fields.get("es_admin") is False:
        updated_fields["es_super_admin"] = False

    updated_user = usuario_repository.update(db, user_id, updated_fields)

    if updated_user is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    return UsuarioMessageResponseSchema(
        message="Usuario actualizado correctamente",
        usuario=to_usuario_response(updated_user)
    )


# --------------------------------------------------
# BORRAR UN USUARIO
# --------------------------------------------------

def delete_usuario(db: Session, user_id: int) -> UsuarioMessageResponseSchema:
    deleted_user = usuario_repository.delete(db, user_id)

    if deleted_user is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    return UsuarioMessageResponseSchema(
        message="Usuario eliminado correctamente",
        usuario=to_usuario_response(deleted_user)
    )
