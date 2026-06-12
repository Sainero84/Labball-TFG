from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import (
    get_current_firebase_user,
    get_current_user
)
from app.repositories import usuario_repository
from app.schemas.auth_schema import (
    AuthRegisterSchema,
    AuthUserResponseSchema
)
from app.schemas.usuario_schema import UsuarioResponseSchema
from app.services import codigo_administrador_service


router = APIRouter(prefix="/auth", tags=["Auth"])


def to_usuario_response(user) -> UsuarioResponseSchema:
    """Expone la ruta encargada de to usuario response y delega la logica principal."""
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


@router.post("/register", response_model=AuthUserResponseSchema)
def register_user(
    user_data: AuthRegisterSchema,
    firebase_user: dict = Depends(get_current_firebase_user),
    db: Session = Depends(get_db)
):
    """Expone la ruta encargada de register user y delega la logica principal."""
    firebase_uid = firebase_user.get("uid")

    if firebase_uid != user_data.firebase_uid:
        raise HTTPException(
            status_code=401,
            detail="El Firebase UID no coincide con el token"
        )

    existing_uid = usuario_repository.get_by_firebase_uid(
        db,
        firebase_uid
    )

    if existing_uid is not None:
        raise HTTPException(
            status_code=400,
            detail="Ya existe un usuario con ese Firebase UID"
        )

    existing_correo = usuario_repository.get_by_correo(db, user_data.correo)

    if existing_correo is not None:
        raise HTTPException(
            status_code=400,
            detail="Ya existe un usuario con ese correo"
        )

    codigo_administrador = None

    if user_data.codigo_administrador:
        codigo_administrador = codigo_administrador_service.ensure_codigo_valid(
            db,
            user_data.codigo_administrador
        )

    es_admin = codigo_administrador is not None
    es_super_admin = es_admin and usuario_repository.count_admins(db) == 0

    user = usuario_repository.create(
        db,
        {
            "firebase_uid": firebase_uid,
            "correo": user_data.correo,
            "es_admin": es_admin,
            "es_super_admin": es_super_admin,
            "es_entrenador": False,
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

    return AuthUserResponseSchema(
        message="Usuario registrado correctamente",
        usuario=to_usuario_response(user)
    )


@router.post("/login", response_model=AuthUserResponseSchema)
def login(current_user=Depends(get_current_user)):
    """Expone la ruta encargada de login y delega la logica principal."""
    return AuthUserResponseSchema(
        message="Token valido",
        usuario=to_usuario_response(current_user)
    )
