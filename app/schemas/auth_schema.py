from pydantic import BaseModel, Field
from typing import Optional

from app.schemas.usuario_schema import UsuarioResponseSchema


class AuthRegisterSchema(BaseModel):
    """Define el esquema de datos auth register schema para validar entradas y respuestas."""
    correo: str = Field(..., min_length=1, max_length=150)
    firebase_uid: str = Field(..., min_length=1, max_length=128)
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AuthUserResponseSchema(BaseModel):
    """Define el esquema de datos auth user response schema para validar entradas y respuestas."""
    message: str
    usuario: UsuarioResponseSchema


class AuthMessageResponseSchema(BaseModel):
    """Define el esquema de datos auth message response schema para validar entradas y respuestas."""
    message: str
