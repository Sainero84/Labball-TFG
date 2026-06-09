from pydantic import BaseModel, Field
from typing import Optional

from app.schemas.usuario_schema import UsuarioResponseSchema


class AuthRegisterSchema(BaseModel):
    correo: str = Field(..., min_length=1, max_length=150)
    firebase_uid: str = Field(..., min_length=1, max_length=128)
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AuthUserResponseSchema(BaseModel):
    message: str
    usuario: UsuarioResponseSchema


class AuthMessageResponseSchema(BaseModel):
    message: str
