from pathlib import Path
from uuid import uuid4
import os
from urllib.parse import urlparse

from fastapi import APIRouter, Depends, HTTPException, Request
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.schemas.usuario_schema import (
    UsuarioCreateSchema,
    UsuarioUpdateSchema,
    UsuarioFotoPerfilUpdateSchema,
    UsuarioMeResponseSchema,
    UsuarioResponseSchema,
    UsuarioListResponseSchema,
    UsuarioMessageResponseSchema,
    UsuarioPerfilUpdateSchema,
    UsuarioTelefonoUpdateSchema
)
from app.services.usuario_service import (
    get_all_usuarios,
    get_usuario_by_id,
    create_usuario,
    update_usuario,
    delete_usuario
)
from app.firebase.firebase_dependencies import (
    get_current_admin_user,
    get_current_super_admin_user,
    get_current_user
)

router = APIRouter(prefix="/usuarios", tags=["Usuarios"])

BASE_DIR = Path(__file__).resolve().parent.parent.parent
PROFILE_IMAGES_DIR = BASE_DIR / "static" / "profile-images"
PROFILE_IMAGES_DIR.mkdir(parents=True, exist_ok=True)
MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024


def normalize_static_url(url: str | None) -> str | None:
    if url is None:
        return None

    parsed_url = urlparse(url)

    if parsed_url.path.startswith("/static/"):
        public_base_url = os.getenv("PUBLIC_BASE_URL")

        if public_base_url:
            return public_base_url.rstrip("/") + parsed_url.path

    return url


def get_public_base_url(request: Request) -> str:
    return (
        os.getenv("PUBLIC_BASE_URL")
        or str(request.base_url).rstrip("/")
    )


def to_usuario_me_response(user) -> UsuarioMeResponseSchema:
    return UsuarioMeResponseSchema(
        id_usuario=user.id_usuario,
        correo=user.correo,
        es_admin=user.es_admin,
        es_super_admin=user.es_super_admin,
        es_entrenador=user.es_entrenador,
        telefono=user.telefono,
        nombre=user.nombre,
        apellido_1=user.apellido_1,
        fecha_nacimiento=user.fecha_nacimiento,
        foto_perfil_url=normalize_static_url(user.foto_perfil_url)
    )


@router.get("/me", response_model=UsuarioMeResponseSchema)
def get_usuario_me(current_user=Depends(get_current_user)):
    return to_usuario_me_response(current_user)


@router.put("/me/telefono", response_model=UsuarioMeResponseSchema)
def update_usuario_me_telefono(
    user_data: UsuarioTelefonoUpdateSchema,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    updated_user = update_usuario(db, current_user.id_usuario, user_data)

    return to_usuario_me_response(updated_user.usuario)


@router.put("/me/perfil", response_model=UsuarioMeResponseSchema)
def update_usuario_me_perfil(
    user_data: UsuarioPerfilUpdateSchema,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    updated_user = update_usuario(db, current_user.id_usuario, user_data)

    return to_usuario_me_response(updated_user.usuario)


@router.put("/me/foto-perfil", response_model=UsuarioMeResponseSchema)
def update_usuario_me_foto_perfil(
    user_data: UsuarioFotoPerfilUpdateSchema,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    updated_user = update_usuario(db, current_user.id_usuario, user_data)

    return to_usuario_me_response(updated_user.usuario)


@router.post("/me/foto-perfil/upload", response_model=UsuarioMeResponseSchema)
async def upload_usuario_me_foto_perfil(
    request: Request,
    current_user=Depends(get_current_user),
    db: Session = Depends(get_db)
):
    content_type = request.headers.get("content-type")

    if content_type is None or not content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="El archivo debe ser una imagen")

    content = await request.body()

    if len(content) > MAX_PROFILE_IMAGE_SIZE:
        raise HTTPException(status_code=400, detail="La imagen no puede superar 5 MB")

    original_filename = request.headers.get("x-file-name", "")
    extension = Path(original_filename).suffix.lower()
    if extension not in [".jpg", ".jpeg", ".png", ".webp"]:
        extension = {
            "image/jpeg": ".jpg",
            "image/png": ".png",
            "image/webp": ".webp"
        }.get(content_type, ".jpg")

    filename = f"user-{current_user.id_usuario}-{uuid4().hex}{extension}"
    file_path = PROFILE_IMAGES_DIR / filename
    file_path.write_bytes(content)

    public_url = get_public_base_url(request) + f"/static/profile-images/{filename}"

    updated_user = update_usuario(
        db,
        current_user.id_usuario,
        UsuarioFotoPerfilUpdateSchema(
            foto_perfil_url=public_url,
            foto_perfil_mime_type=content_type
        )
    )

    return to_usuario_me_response(updated_user.usuario)


@router.get("/", response_model=UsuarioListResponseSchema)
def get_usuarios(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_usuarios(db)


@router.get("/{user_id}", response_model=UsuarioResponseSchema)
def get_usuario(
    user_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_usuario_by_id(db, user_id)


@router.post("/", response_model=UsuarioMessageResponseSchema)
def create_new_usuario(
    user_data: UsuarioCreateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return create_usuario(db, user_data)


@router.put("/{user_id}", response_model=UsuarioMessageResponseSchema)
def update_existing_usuario(
    user_id: int,
    user_data: UsuarioUpdateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return update_usuario(db, user_id, user_data)


@router.delete("/{user_id}", response_model=UsuarioMessageResponseSchema)
def delete_existing_usuario(
    user_id: int,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return delete_usuario(db, user_id)
