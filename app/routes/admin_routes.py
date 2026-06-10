from pathlib import Path
from uuid import uuid4
from email.parser import BytesParser
from email.policy import default
import os
import shutil
import subprocess

from fastapi import APIRouter, Depends, HTTPException, Request
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_dependencies import (
    get_current_admin_user,
    get_current_super_admin_user
)
from app.schemas.entrenamiento_schema import (
    EntrenamientoCreateSchema,
    EntrenamientoListResponseSchema,
    EntrenamientoMessageResponseSchema,
    EntrenamientoUpdateSchema
)
from app.schemas.inscripcion_schema import (
    ReservaEntrenamientosAsignadosResponseSchema,
    ReservaEntrenamientosAsignarSchema,
    ReservaAdminListResponseSchema,
    ReservaMessageResponseSchema,
    ReservaPagadoUpdateSchema,
    ReservaResponseSchema
)
from app.schemas.jugador_schema import (
    JugadorCreateSchema,
    JugadorEstadisticasUpdateSchema,
    JugadorListResponseSchema,
    JugadorMessageResponseSchema,
    JugadorResponseSchema,
    JugadorUpdateSchema
)
from app.schemas.codigo_administrador_schema import (
    CodigoAdministradorGenerateResponseSchema,
    CodigoAdministradorGenerateSchema,
    CodigoAdministradorListResponseSchema,
    CodigoAdministradorValidateResponseSchema,
    CodigoAdministradorValidateSchema
)
from app.schemas.catalogo_schema import (
    EntrenadorListResponseSchema,
    UbicacionCreateSchema,
    UbicacionDeleteResponseSchema,
    UbicacionListResponseSchema,
    UbicacionMessageResponseSchema
)
from app.schemas.descuento_schema import (
    DescuentoAdminCreateSchema,
    DescuentoDeleteResponseSchema,
    DescuentoListResponseSchema,
    DescuentoMessageResponseSchema,
    DescuentoUpdateSchema
)
from app.schemas.media_schema import (
    MediaCreateSchema,
    MediaMessageResponseSchema,
    MediaUpdateSchema
)
from app.schemas.tarifa_schema import (
    TarifaCreateSchema,
    TarifaListResponseSchema,
    TarifaMessageResponseSchema,
    TarifaResponseSchema,
    TarifaUpdateSchema
)
from app.schemas.usuario_schema import (
    AdminUsuarioCreateSchema,
    AdminUsuarioDeleteResponseSchema,
    AdminUsuarioListResponseSchema,
    AdminUsuarioMessageResponseSchema,
    AdminUsuarioRolUpdateSchema
)
from app.services import (
    admin_usuario_service,
    catalogo_service,
    codigo_administrador_service,
    entrenamiento_service,
    jugador_service,
    media_service,
    tarifa_service
)
from app.services.inscripcion_service import (
    asignar_entrenamientos_reserva,
    get_all_reservas_admin,
    get_entrenamientos_reserva_admin,
    get_reserva_admin_by_id,
    reemplazar_entrenamientos_reserva_admin,
    update_reserva_pagado
)


router = APIRouter(prefix="/admin", tags=["Admin"])

BASE_DIR = Path(__file__).resolve().parent.parent.parent
MEDIA_VIDEOS_DIR = BASE_DIR / "static" / "media" / "videos"
MEDIA_THUMBNAILS_DIR = BASE_DIR / "static" / "media" / "thumbnails"
MEDIA_VIDEOS_DIR.mkdir(parents=True, exist_ok=True)
MEDIA_THUMBNAILS_DIR.mkdir(parents=True, exist_ok=True)

MAX_VIDEO_SIZE = 200 * 1024 * 1024
MAX_THUMBNAIL_SIZE = 10 * 1024 * 1024
ALLOWED_VIDEO_EXTENSIONS = {".mp4", ".mov", ".webm", ".mkv"}
ALLOWED_THUMBNAIL_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"}


@router.get("/usuarios", response_model=AdminUsuarioListResponseSchema)
def get_admin_usuarios(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return admin_usuario_service.get_admin_usuarios(db)


@router.post("/usuarios", response_model=AdminUsuarioMessageResponseSchema)
def create_admin_usuario(
    usuario_data: AdminUsuarioCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return admin_usuario_service.create_admin_usuario(db, usuario_data, admin)


@router.put("/usuarios/{usuario_id}/rol", response_model=AdminUsuarioMessageResponseSchema)
def update_admin_usuario_rol(
    usuario_id: int,
    rol_data: AdminUsuarioRolUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return admin_usuario_service.update_admin_usuario_rol(
        db,
        usuario_id,
        rol_data,
        admin
    )


@router.delete("/usuarios/{usuario_id}", response_model=AdminUsuarioDeleteResponseSchema)
def delete_admin_usuario(
    usuario_id: int,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return admin_usuario_service.delete_admin_usuario(db, usuario_id, admin)


def get_safe_extension(filename: str, content_type: str | None, allowed_extensions: set[str]) -> str:
    extension = Path(filename or "").suffix.lower()

    if extension in allowed_extensions:
        return extension

    extension_by_content_type = {
        "video/mp4": ".mp4",
        "video/quicktime": ".mov",
        "video/webm": ".webm",
        "video/x-matroska": ".mkv",
        "image/jpeg": ".jpg",
        "image/png": ".png",
        "image/webp": ".webp"
    }

    inferred_extension = extension_by_content_type.get(content_type or "")
    if inferred_extension in allowed_extensions:
        return inferred_extension

    return next(iter(allowed_extensions))


def parse_multipart_body(content_type: str, body: bytes) -> tuple[dict[str, str], dict[str, dict]]:
    if not content_type.startswith("multipart/form-data"):
        raise HTTPException(status_code=400, detail="La peticion debe ser multipart/form-data")

    raw_message = (
        f"Content-Type: {content_type}\r\n"
        "MIME-Version: 1.0\r\n\r\n"
    ).encode("utf-8") + body
    message = BytesParser(policy=default).parsebytes(raw_message)

    fields: dict[str, str] = {}
    files: dict[str, dict] = {}

    if not message.is_multipart():
        raise HTTPException(status_code=400, detail="Formulario multipart invalido")

    for part in message.iter_parts():
        name = part.get_param("name", header="content-disposition")
        if not name:
            continue

        filename = part.get_filename()
        content = part.get_payload(decode=True) or b""

        if filename:
            files[name] = {
                "filename": filename,
                "content_type": part.get_content_type(),
                "content": content
            }
        else:
            charset = part.get_content_charset() or "utf-8"
            fields[name] = content.decode(charset, errors="replace")

    return fields, files


def save_uploaded_content(
    filename: str,
    content_type: str | None,
    content: bytes,
    target_dir: Path,
    allowed_extensions: set[str],
    max_size: int
) -> tuple[str, str]:
    if len(content) > max_size:
        raise HTTPException(status_code=400, detail="El archivo supera el tamaño máximo permitido")

    extension = get_safe_extension(
        filename,
        content_type,
        allowed_extensions
    )
    stored_filename = f"{uuid4().hex}{extension}"
    file_path = target_dir / stored_filename
    file_path.write_bytes(content)

    return stored_filename, content_type or "application/octet-stream"


def generate_thumbnail_from_video(video_path: Path) -> str:
    configured_ffmpeg = os.getenv("FFMPEG_PATH")
    ffmpeg_path = configured_ffmpeg or shutil.which("ffmpeg")

    if not ffmpeg_path:
        raise HTTPException(
            status_code=500,
            detail="FFmpeg no esta instalado o no se encuentra en PATH"
        )

    thumbnail_filename = f"{uuid4().hex}.jpg"
    thumbnail_path = MEDIA_THUMBNAILS_DIR / thumbnail_filename

    try:
        subprocess.run(
            [
                ffmpeg_path,
                "-y",
                "-ss",
                "00:00:01",
                "-i",
                str(video_path),
                "-frames:v",
                "1",
                "-vf",
                "scale=640:-2",
                str(thumbnail_path)
            ],
            check=True,
            capture_output=True,
            text=True,
            timeout=30
        )
    except OSError as exception:
        thumbnail_path.unlink(missing_ok=True)
        raise HTTPException(
            status_code=500,
            detail="No se pudo ejecutar FFmpeg"
        ) from exception
    except (subprocess.CalledProcessError, subprocess.TimeoutExpired) as exception:
        thumbnail_path.unlink(missing_ok=True)
        raise HTTPException(
            status_code=400,
            detail="No se pudo generar la miniatura desde el video"
        ) from exception

    if not thumbnail_path.exists() or thumbnail_path.stat().st_size == 0:
        thumbnail_path.unlink(missing_ok=True)
        raise HTTPException(
            status_code=400,
            detail="FFmpeg no genero una miniatura valida"
        )

    return thumbnail_filename


@router.get("/codigos-administrador", response_model=CodigoAdministradorListResponseSchema)
def get_admin_codigos_administrador(
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return codigo_administrador_service.get_all_codigos(db)


@router.post(
    "/codigos-administrador",
    response_model=CodigoAdministradorGenerateResponseSchema
)
def generate_admin_codigos_administrador(
    codigo_data: CodigoAdministradorGenerateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return codigo_administrador_service.generate_codigos(db, codigo_data)


@router.post(
    "/codigos-administrador/validar",
    response_model=CodigoAdministradorValidateResponseSchema
)
def validate_admin_codigo_administrador(
    codigo_data: CodigoAdministradorValidateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return codigo_administrador_service.validate_codigo(db, codigo_data)


@router.get("/entrenadores", response_model=EntrenadorListResponseSchema)
def get_admin_entrenadores(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.get_all_entrenadores(db)


@router.get("/ubicaciones", response_model=UbicacionListResponseSchema)
def get_admin_ubicaciones(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.get_all_ubicaciones(db)


@router.post("/ubicaciones", response_model=UbicacionMessageResponseSchema)
def create_admin_ubicacion(
    ubicacion_data: UbicacionCreateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.create_ubicacion(db, ubicacion_data)


@router.delete("/ubicaciones/{ubicacion_id}", response_model=UbicacionDeleteResponseSchema)
def delete_admin_ubicacion(
    ubicacion_id: int,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.delete_ubicacion(db, ubicacion_id)


@router.get("/descuentos", response_model=DescuentoListResponseSchema)
def get_admin_descuentos(
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.get_all_admin_descuentos(db)


@router.post("/descuentos", response_model=DescuentoMessageResponseSchema)
def create_admin_descuento(
    descuento_data: DescuentoAdminCreateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.create_admin_descuento(db, descuento_data)


@router.put("/descuentos/{descuento_id}", response_model=DescuentoMessageResponseSchema)
def update_admin_descuento(
    descuento_id: int,
    descuento_data: DescuentoUpdateSchema,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.update_admin_descuento(db, descuento_id, descuento_data)


@router.delete("/descuentos/{descuento_id}", response_model=DescuentoDeleteResponseSchema)
def delete_admin_descuento(
    descuento_id: int,
    admin=Depends(get_current_super_admin_user),
    db: Session = Depends(get_db)
):
    return catalogo_service.delete_admin_descuento(db, descuento_id)


@router.get("/tarifas", response_model=TarifaListResponseSchema)
def get_admin_tarifas(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return tarifa_service.get_all_tarifas(db)


@router.get("/tarifas/{tarifa_id}", response_model=TarifaResponseSchema)
def get_admin_tarifa(
    tarifa_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return tarifa_service.get_tarifa_by_id(db, tarifa_id)


@router.post("/tarifas", response_model=TarifaMessageResponseSchema)
def create_admin_tarifa(
    tarifa_data: TarifaCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return tarifa_service.create_tarifa(db, tarifa_data)


@router.put("/tarifas/{tarifa_id}", response_model=TarifaMessageResponseSchema)
def update_admin_tarifa(
    tarifa_id: int,
    tarifa_data: TarifaUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return tarifa_service.update_tarifa(db, tarifa_id, tarifa_data)


@router.delete("/tarifas/{tarifa_id}", response_model=TarifaMessageResponseSchema)
def delete_admin_tarifa(
    tarifa_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return tarifa_service.delete_tarifa(db, tarifa_id)


@router.get("/reservas", response_model=ReservaAdminListResponseSchema)
def get_admin_reservas(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_all_reservas_admin(db)


@router.get("/reservas/{reserva_id}", response_model=ReservaResponseSchema)
def get_admin_reserva(
    reserva_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_reserva_admin_by_id(db, reserva_id)


@router.put("/reservas/{reserva_id}/pagado", response_model=ReservaMessageResponseSchema)
def update_admin_reserva_pagado(
    reserva_id: int,
    pagado_data: ReservaPagadoUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return update_reserva_pagado(db, reserva_id, pagado_data.pagado)


@router.post(
    "/reservas/{reserva_id}/entrenamientos/asignar",
    response_model=ReservaEntrenamientosAsignadosResponseSchema
)
def asignar_admin_entrenamientos_reserva(
    reserva_id: int,
    asignacion_data: ReservaEntrenamientosAsignarSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return asignar_entrenamientos_reserva(db, reserva_id, asignacion_data)


@router.get(
    "/reservas/{reserva_id}/entrenamientos",
    response_model=EntrenamientoListResponseSchema
)
def get_admin_entrenamientos_reserva(
    reserva_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return get_entrenamientos_reserva_admin(db, reserva_id)


@router.put(
    "/reservas/{reserva_id}/entrenamientos",
    response_model=ReservaEntrenamientosAsignadosResponseSchema
)
def reemplazar_admin_entrenamientos_reserva(
    reserva_id: int,
    asignacion_data: ReservaEntrenamientosAsignarSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return reemplazar_entrenamientos_reserva_admin(db, reserva_id, asignacion_data)


@router.post("/entrenamientos", response_model=EntrenamientoMessageResponseSchema)
def create_admin_entrenamiento(
    entrenamiento_data: EntrenamientoCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return entrenamiento_service.create_entrenamiento(db, entrenamiento_data)


@router.put("/entrenamientos/{entrenamiento_id}", response_model=EntrenamientoMessageResponseSchema)
def update_admin_entrenamiento(
    entrenamiento_id: int,
    entrenamiento_data: EntrenamientoUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return entrenamiento_service.update_entrenamiento(
        db,
        entrenamiento_id,
        entrenamiento_data
    )


@router.delete("/entrenamientos/{entrenamiento_id}", response_model=EntrenamientoMessageResponseSchema)
def delete_admin_entrenamiento(
    entrenamiento_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return entrenamiento_service.delete_entrenamiento(db, entrenamiento_id)


@router.post("/media/videos", response_model=MediaMessageResponseSchema)
def create_admin_video(
    media_data: MediaCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return media_service.create_media(db, media_data)


@router.post("/media/videos/upload", response_model=MediaMessageResponseSchema)
async def upload_admin_video(
    request: Request,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    fields, files = parse_multipart_body(
        request.headers.get("content-type", ""),
        await request.body()
    )

    titulo = fields.get("titulo", "").strip()
    descripcion = fields.get("descripcion")
    video_file = files.get("video_file") or files.get("video")
    miniatura_file = files.get("miniatura_file") or files.get("miniatura")

    if not titulo:
        raise HTTPException(status_code=400, detail="El titulo es obligatorio")

    if len(titulo) > 150:
        raise HTTPException(status_code=400, detail="El titulo no puede superar 150 caracteres")

    if video_file is None:
        raise HTTPException(status_code=400, detail="El archivo de video es obligatorio")

    if video_file["content_type"] is None or not video_file["content_type"].startswith("video/"):
        raise HTTPException(status_code=400, detail="El archivo principal debe ser un video")

    if miniatura_file is not None and (
        miniatura_file["content_type"] is None
        or not miniatura_file["content_type"].startswith("image/")
    ):
        raise HTTPException(status_code=400, detail="La miniatura debe ser una imagen")

    video_filename, video_mime_type = save_uploaded_content(
        video_file["filename"],
        video_file["content_type"],
        video_file["content"],
        MEDIA_VIDEOS_DIR,
        ALLOWED_VIDEO_EXTENSIONS,
        MAX_VIDEO_SIZE
    )
    video_path = MEDIA_VIDEOS_DIR / video_filename

    thumbnail_url = None
    thumbnail_filename = None

    try:
        if miniatura_file is not None:
            thumbnail_filename, _ = save_uploaded_content(
                miniatura_file["filename"],
                miniatura_file["content_type"],
                miniatura_file["content"],
                MEDIA_THUMBNAILS_DIR,
                ALLOWED_THUMBNAIL_EXTENSIONS,
                MAX_THUMBNAIL_SIZE
            )
        else:
            thumbnail_filename = generate_thumbnail_from_video(video_path)
    except HTTPException:
        video_path.unlink(missing_ok=True)
        raise

    public_base_url = (
        os.getenv("PUBLIC_BASE_URL")
        or str(request.base_url).rstrip("/")
    )

    if thumbnail_filename is not None:
        thumbnail_url = (
            public_base_url
            + f"/static/media/thumbnails/{thumbnail_filename}"
        )

    video_url = (
        public_base_url
        + f"/static/media/videos/{video_filename}"
    )

    try:
        return media_service.create_media(
            db,
            MediaCreateSchema(
                id_usuario=admin.id_usuario,
                titulo=titulo,
                descripcion=descripcion,
                url_archivo=video_url,
                url_miniatura=thumbnail_url,
                mime_type=video_mime_type
            )
        )
    except Exception:
        video_path.unlink(missing_ok=True)

        if thumbnail_filename is not None:
            (MEDIA_THUMBNAILS_DIR / thumbnail_filename).unlink(missing_ok=True)

        raise


@router.put("/media/videos/{media_id}", response_model=MediaMessageResponseSchema)
def update_admin_video(
    media_id: int,
    media_data: MediaUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return media_service.update_media(db, media_id, media_data)


@router.delete("/media/videos/{media_id}", response_model=MediaMessageResponseSchema)
def delete_admin_video(
    media_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return media_service.delete_media(db, media_id)


@router.get("/jugadores", response_model=JugadorListResponseSchema)
def get_admin_jugadores(
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return jugador_service.get_all_jugadores(db)


@router.get("/jugadores/{jugador_id}", response_model=JugadorResponseSchema)
def get_admin_jugador(
    jugador_id: int,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return jugador_service.get_jugador_by_id(db, jugador_id)


@router.post("/jugadores", response_model=JugadorMessageResponseSchema)
def create_admin_jugador(
    jugador_data: JugadorCreateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return jugador_service.create_jugador(db, jugador_data)


@router.put("/jugadores/{jugador_id}", response_model=JugadorMessageResponseSchema)
def update_admin_jugador(
    jugador_id: int,
    jugador_data: JugadorUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return jugador_service.update_jugador(db, jugador_id, jugador_data)


@router.put("/jugadores/{jugador_id}/estadisticas", response_model=JugadorMessageResponseSchema)
def update_admin_jugador_estadisticas(
    jugador_id: int,
    estadisticas_data: JugadorEstadisticasUpdateSchema,
    admin=Depends(get_current_admin_user),
    db: Session = Depends(get_db)
):
    return jugador_service.update_jugador(db, jugador_id, estadisticas_data)
