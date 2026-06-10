import json
import os
import secrets
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen

from fastapi import HTTPException
from firebase_admin import auth
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.repositories import usuario_repository
from app.services import codigo_administrador_service
from app.schemas.usuario_schema import (
    AdminUsuarioCreateSchema,
    AdminUsuarioListResponseSchema,
    AdminUsuarioMessageResponseSchema,
    AdminUsuarioResponseSchema,
    AdminUsuarioRolUpdateSchema
)


def to_admin_usuario_response(user) -> AdminUsuarioResponseSchema:
    jugador = getattr(user, "jugador", None)

    return AdminUsuarioResponseSchema(
        id_usuario=user.id_usuario,
        correo=user.correo,
        es_admin=user.es_admin,
        es_super_admin=user.es_super_admin,
        es_entrenador=user.es_entrenador,
        nombre=user.nombre or (jugador.nombre if jugador is not None else None),
        apellido_1=user.apellido_1 or (jugador.apellidos if jugador is not None else None),
        telefono=user.telefono,
        foto_perfil_url=user.foto_perfil_url
    )


def get_admin_usuarios(db: Session) -> AdminUsuarioListResponseSchema:
    usuarios = usuario_repository.get_all(db)

    return AdminUsuarioListResponseSchema(
        usuarios=[to_admin_usuario_response(usuario) for usuario in usuarios]
    )


def get_firebase_web_api_key() -> str:
    api_key = os.getenv("FIREBASE_WEB_API_KEY")

    if not api_key:
        raise HTTPException(
            status_code=500,
            detail="Falta configurar FIREBASE_WEB_API_KEY para enviar el correo de acceso"
        )

    return api_key


def send_password_reset_email(correo: str, api_key: str):
    endpoint = (
        "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?"
        + urlencode({"key": api_key})
    )
    payload = json.dumps({
        "requestType": "PASSWORD_RESET",
        "email": correo
    }).encode("utf-8")
    request = Request(
        endpoint,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "X-Firebase-Locale": "es"
        },
        method="POST"
    )

    try:
        with urlopen(request, timeout=10):
            return
    except HTTPError as exception:
        error_message = "Error desconocido"

        try:
            response = json.loads(exception.read().decode("utf-8"))
            error_message = response.get("error", {}).get("message", error_message)
        except (json.JSONDecodeError, UnicodeDecodeError):
            pass

        raise HTTPException(
            status_code=502,
            detail=f"Firebase no pudo enviar el correo para establecer la contraseña: {error_message}"
        ) from exception
    except URLError as exception:
        raise HTTPException(
            status_code=502,
            detail="No se pudo conectar con Firebase para enviar el correo de acceso"
        ) from exception


def delete_firebase_user_if_exists(firebase_uid: str):
    try:
        auth.delete_user(firebase_uid)
    except auth.UserNotFoundError:
        return


def create_admin_usuario(
    db: Session,
    usuario_data: AdminUsuarioCreateSchema,
    current_admin
) -> AdminUsuarioMessageResponseSchema:
    correo = usuario_data.correo.strip().lower()
    codigo_administrador = None
    es_super_admin = usuario_data.es_super_admin
    es_admin = usuario_data.es_admin or es_super_admin

    if es_super_admin and not current_admin.es_super_admin:
        raise HTTPException(
            status_code=403,
            detail="Solo un superadministrador puede crear superadministradores"
        )

    if usuario_data.es_entrenador and not current_admin.es_super_admin:
        raise HTTPException(
            status_code=403,
            detail="Solo un superadministrador puede crear entrenadores"
        )

    if usuario_data.es_entrenador and not es_admin:
        raise HTTPException(
            status_code=400,
            detail="Un entrenador debe ser administrador"
        )

    if es_admin:
        if not usuario_data.codigo_administrador:
            raise HTTPException(
                status_code=400,
                detail="El codigo de administrador es obligatorio"
            )

        codigo_administrador = codigo_administrador_service.ensure_codigo_valid(
            db,
            usuario_data.codigo_administrador
        )

    if usuario_repository.get_by_correo(db, correo) is not None:
        raise HTTPException(status_code=409, detail="Ya existe un usuario con ese correo")

    try:
        auth.get_user_by_email(correo)
    except auth.UserNotFoundError:
        pass
    else:
        raise HTTPException(
            status_code=409,
            detail="Ya existe una cuenta de Firebase Authentication con ese correo"
        )

    api_key = get_firebase_web_api_key()
    firebase_user = None

    try:
        firebase_user = auth.create_user(
            email=correo,
            email_verified=False,
            password=secrets.token_urlsafe(32),
            disabled=False
        )
        usuario = usuario_repository.create_without_commit(
            db,
            {
                "firebase_uid": firebase_user.uid,
                "correo": correo,
                "es_admin": es_admin,
                "es_super_admin": es_super_admin,
                "es_entrenador": usuario_data.es_entrenador,
                "id_codigo_administrador": (
                    codigo_administrador.id_codigo_administrador
                    if codigo_administrador else None
                ),
                "telefono": None,
                "nombre": None,
                "apellido_1": None,
                "fecha_nacimiento": None,
                "foto_perfil_url": None,
                "foto_perfil_mime_type": None
            }
        )
        db.flush()
        send_password_reset_email(correo, api_key)
        db.commit()
        db.refresh(usuario)
    except auth.EmailAlreadyExistsError as exception:
        db.rollback()
        raise HTTPException(
            status_code=409,
            detail="Ya existe una cuenta de Firebase Authentication con ese correo"
        ) from exception
    except IntegrityError as exception:
        db.rollback()

        if firebase_user is not None:
            delete_firebase_user_if_exists(firebase_user.uid)

        raise HTTPException(
            status_code=409,
            detail="No se pudo crear el usuario porque el correo o UID ya existe"
        ) from exception
    except HTTPException:
        db.rollback()

        if firebase_user is not None:
            delete_firebase_user_if_exists(firebase_user.uid)

        raise
    except Exception as exception:
        db.rollback()

        if firebase_user is not None:
            delete_firebase_user_if_exists(firebase_user.uid)

        raise HTTPException(
            status_code=502,
            detail="No se pudo crear la cuenta en Firebase Authentication"
        ) from exception

    return AdminUsuarioMessageResponseSchema(
        message="Usuario creado correctamente. Se ha enviado un correo para establecer la contraseña",
        usuario=to_admin_usuario_response(usuario)
    )


def update_admin_usuario_rol(
    db: Session,
    usuario_id: int,
    rol_data: AdminUsuarioRolUpdateSchema,
    current_admin
) -> AdminUsuarioMessageResponseSchema:
    usuario = usuario_repository.get_by_id(db, usuario_id)

    if usuario is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    if (
        rol_data.es_admin is None
        and rol_data.es_super_admin is None
        and rol_data.es_entrenador is None
    ):
        raise HTTPException(status_code=400, detail="No hay cambios de rol para aplicar")

    nuevo_es_admin = (
        usuario.es_admin
        if rol_data.es_admin is None
        else rol_data.es_admin
    )
    nuevo_es_super_admin = (
        usuario.es_super_admin
        if rol_data.es_super_admin is None
        else rol_data.es_super_admin
    )
    nuevo_es_entrenador = (
        usuario.es_entrenador
        if rol_data.es_entrenador is None
        else rol_data.es_entrenador
    )

    if not current_admin.es_super_admin:
        if usuario.es_admin or usuario.es_super_admin:
            raise HTTPException(
                status_code=403,
                detail="Solo un superadministrador puede modificar administradores"
            )

        if nuevo_es_super_admin:
            raise HTTPException(
                status_code=403,
                detail="Solo un superadministrador puede asignar el rol de superadministrador"
            )

        if nuevo_es_entrenador != usuario.es_entrenador:
            raise HTTPException(
                status_code=403,
                detail="Solo un superadministrador puede modificar entrenadores"
            )

    if rol_data.es_admin is False and rol_data.es_super_admin is not True:
        nuevo_es_super_admin = False

    if nuevo_es_super_admin:
        nuevo_es_admin = True

    if nuevo_es_entrenador and not nuevo_es_admin:
        raise HTTPException(
            status_code=400,
            detail="Un entrenador debe ser administrador"
        )

    codigo_administrador = None

    promociona_a_admin = nuevo_es_admin and not usuario.es_admin
    promociona_a_super_admin = nuevo_es_super_admin and not usuario.es_super_admin

    if promociona_a_admin or promociona_a_super_admin:
        if not rol_data.codigo_administrador:
            raise HTTPException(
                status_code=400,
                detail="El codigo de administrador es obligatorio"
            )

        codigo_administrador = codigo_administrador_service.ensure_codigo_valid(
            db,
            rol_data.codigo_administrador
        )

    if (
        usuario.id_usuario == current_admin.id_usuario
        and (not nuevo_es_admin or not nuevo_es_super_admin)
    ):
        raise HTTPException(
            status_code=409,
            detail="Un superadministrador no puede quitarse a si mismo sus permisos"
        )

    usuario.es_admin = nuevo_es_admin
    usuario.es_super_admin = nuevo_es_super_admin
    usuario.es_entrenador = nuevo_es_entrenador

    if codigo_administrador is not None:
        usuario.id_codigo_administrador = codigo_administrador.id_codigo_administrador
    elif not nuevo_es_admin:
        usuario.id_codigo_administrador = None

    try:
        db.commit()
    except IntegrityError as exception:
        db.rollback()
        raise HTTPException(
            status_code=409,
            detail="Ese codigo de administrador ya esta asociado a otro usuario"
        ) from exception

    db.refresh(usuario)

    return AdminUsuarioMessageResponseSchema(
        message="Rol actualizado correctamente",
        usuario=to_admin_usuario_response(usuario)
    )


def delete_admin_usuario(db: Session, usuario_id: int, current_admin) -> dict[str, str]:
    usuario = usuario_repository.get_by_id(db, usuario_id)

    if usuario is None:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")

    if usuario.id_usuario == current_admin.id_usuario:
        raise HTTPException(
            status_code=409,
            detail="Un administrador no puede eliminar su propia cuenta"
        )

    related_data = usuario_repository.get_related_data_counts(db, usuario_id)
    existing_relations = [
        f"{name}: {count}"
        for name, count in related_data.items()
        if count > 0
    ]

    if existing_relations:
        raise HTTPException(
            status_code=409,
            detail=(
                "No se puede eliminar el usuario porque tiene informacion de negocio asociada "
                f"({', '.join(existing_relations)}). "
                "Se recomienda desactivar la cuenta y conservar el historial."
            )
        )

    try:
        usuario_repository.delete_without_commit(db, usuario)
        db.flush()
        delete_firebase_user_if_exists(usuario.firebase_uid)
        db.commit()
    except HTTPException:
        db.rollback()
        raise
    except Exception as exception:
        db.rollback()
        raise HTTPException(
            status_code=502,
            detail="No se pudo eliminar correctamente la cuenta de Firebase Authentication"
        ) from exception

    return {"message": "Usuario eliminado correctamente"}
