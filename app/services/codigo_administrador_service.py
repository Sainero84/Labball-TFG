import secrets
import string

from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.repositories import codigo_administrador_repository, usuario_repository
from app.schemas.codigo_administrador_schema import (
    CodigoAdministradorGenerateResponseSchema,
    CodigoAdministradorGenerateSchema,
    CodigoAdministradorListResponseSchema,
    CodigoAdministradorResponseSchema,
    CodigoAdministradorValidateResponseSchema
)


CODE_LENGTH = 12
CODE_ALPHABET = string.ascii_uppercase + string.digits


def to_codigo_response(codigo_administrador) -> CodigoAdministradorResponseSchema:
    """Aplica la logica de negocio necesaria para to codigo response."""
    return CodigoAdministradorResponseSchema(
        id_codigo_administrador=codigo_administrador.id_codigo_administrador,
        codigo=codigo_administrador.codigo
    )


def generate_raw_code() -> str:
    """Aplica la logica de negocio necesaria para generate raw code."""
    return "".join(secrets.choice(CODE_ALPHABET) for _ in range(CODE_LENGTH))


def is_codigo_valid(db: Session, codigo: str) -> bool:
    """Aplica la logica de negocio necesaria para is codigo valid."""
    codigo_administrador = codigo_administrador_repository.get_by_codigo(
        db,
        codigo.strip().upper()
    )

    if codigo_administrador is None:
        return False

    usuario = usuario_repository.get_by_codigo_administrador_id(
        db,
        codigo_administrador.id_codigo_administrador
    )

    return usuario is None


def ensure_codigo_valid(db: Session, codigo: str):
    """Aplica la logica de negocio necesaria para ensure codigo valid."""
    normalized_code = codigo.strip().upper()
    codigo_administrador = codigo_administrador_repository.get_by_codigo(
        db,
        normalized_code
    )

    if codigo_administrador is None:
        raise HTTPException(status_code=400, detail="Codigo de administrador no valido")

    usuario = usuario_repository.get_by_codigo_administrador_id(
        db,
        codigo_administrador.id_codigo_administrador
    )

    if usuario is not None:
        raise HTTPException(status_code=400, detail="Codigo de administrador ya usado")

    return codigo_administrador


def get_all_codigos(db: Session) -> CodigoAdministradorListResponseSchema:
    """Aplica la logica de negocio necesaria para get all codigos."""
    codigos = codigo_administrador_repository.get_all(db)

    return CodigoAdministradorListResponseSchema(
        codigos=[to_codigo_response(codigo) for codigo in codigos]
    )


def generate_codigos(
    db: Session,
    generate_data: CodigoAdministradorGenerateSchema
) -> CodigoAdministradorGenerateResponseSchema:
    """Aplica la logica de negocio necesaria para generate codigos."""
    codigos = []

    for _ in range(generate_data.cantidad):
        codigo = generate_raw_code()

        while codigo_administrador_repository.get_by_codigo(db, codigo) is not None:
            codigo = generate_raw_code()

        codigos.append(codigo_administrador_repository.create(db, codigo))

    return CodigoAdministradorGenerateResponseSchema(
        message="Codigos de administrador generados correctamente",
        codigos=[to_codigo_response(codigo) for codigo in codigos]
    )


def validate_codigo(
    db: Session,
    codigo_data
) -> CodigoAdministradorValidateResponseSchema:
    """Aplica la logica de negocio necesaria para validate codigo."""
    codigo = codigo_data.codigo.strip().upper()
    valido = is_codigo_valid(db, codigo)

    return CodigoAdministradorValidateResponseSchema(
        codigo=codigo,
        valido=valido,
        message="Codigo valido" if valido else "Codigo no valido"
    )
