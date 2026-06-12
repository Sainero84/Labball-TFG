from pydantic import BaseModel, Field, field_validator
from datetime import date
from typing import Optional


class UsuarioCreateSchema(BaseModel):
    """Define el esquema de datos usuario create schema para validar entradas y respuestas."""
    correo: str = Field(..., min_length=1, max_length=150)
    firebase_uid: str = Field(..., min_length=1, max_length=128)
    es_admin: bool = False
    es_super_admin: bool = False
    es_entrenador: bool = False
    id_codigo_administrador: Optional[int] = None
    telefono: Optional[str] = Field(default=None, max_length=20)
    nombre: Optional[str] = Field(default=None, max_length=100)
    apellido_1: Optional[str] = Field(default=None, max_length=150)
    fecha_nacimiento: Optional[date] = None
    foto_perfil_url: Optional[str] = Field(default=None, max_length=500)
    foto_perfil_mime_type: Optional[str] = Field(default=None, max_length=100)

    @field_validator("fecha_nacimiento", mode="before")
    @classmethod
    def empty_fecha_nacimiento_to_none(cls, value):
        """Coordina la operacion empty fecha nacimiento to none del modulo."""
        if value == "":
            return None

        return value


class UsuarioUpdateSchema(BaseModel):
    """Define el esquema de datos usuario update schema para validar entradas y respuestas."""
    correo: Optional[str] = Field(default=None, min_length=1, max_length=150)
    es_admin: Optional[bool] = None
    es_super_admin: Optional[bool] = None
    es_entrenador: Optional[bool] = None
    id_codigo_administrador: Optional[int] = None
    telefono: Optional[str] = Field(default=None, max_length=20)
    nombre: Optional[str] = Field(default=None, max_length=100)
    apellido_1: Optional[str] = Field(default=None, max_length=150)
    fecha_nacimiento: Optional[date] = None
    foto_perfil_url: Optional[str] = Field(default=None, max_length=500)
    foto_perfil_mime_type: Optional[str] = Field(default=None, max_length=100)

    @field_validator("fecha_nacimiento", mode="before")
    @classmethod
    def empty_fecha_nacimiento_to_none(cls, value):
        """Coordina la operacion empty fecha nacimiento to none del modulo."""
        if value == "":
            return None

        return value


class UsuarioMeResponseSchema(BaseModel):
    """Define el esquema de datos usuario me response schema para validar entradas y respuestas."""
    id_usuario: int
    correo: str
    es_admin: bool
    es_super_admin: bool
    es_entrenador: bool
    telefono: Optional[str] = None
    nombre: Optional[str] = None
    apellido_1: Optional[str] = None
    fecha_nacimiento: Optional[date] = None
    foto_perfil_url: Optional[str] = None


class UsuarioTelefonoUpdateSchema(BaseModel):
    """Define el esquema de datos usuario telefono update schema para validar entradas y respuestas."""
    telefono: Optional[str] = Field(default=None, max_length=20)


class UsuarioPerfilUpdateSchema(BaseModel):
    """Define el esquema de datos usuario perfil update schema para validar entradas y respuestas."""
    nombre: Optional[str] = Field(default=None, max_length=100)
    apellido_1: Optional[str] = Field(default=None, max_length=150)
    fecha_nacimiento: Optional[date] = None
    telefono: Optional[str] = Field(default=None, max_length=20)

    @field_validator("fecha_nacimiento", mode="before")
    @classmethod
    def empty_fecha_nacimiento_to_none(cls, value):
        """Coordina la operacion empty fecha nacimiento to none del modulo."""
        if value == "":
            return None

        return value


class UsuarioFotoPerfilUpdateSchema(BaseModel):
    """Define el esquema de datos usuario foto perfil update schema para validar entradas y respuestas."""
    foto_perfil_url: str = Field(..., min_length=1, max_length=500)
    foto_perfil_mime_type: Optional[str] = Field(default=None, max_length=100)


class UsuarioResponseSchema(BaseModel):
    """Define el esquema de datos usuario response schema para validar entradas y respuestas."""
    id_usuario: int
    firebase_uid: str
    correo: str
    es_admin: bool
    es_super_admin: bool
    es_entrenador: bool
    id_codigo_administrador: Optional[int] = None
    codigo_administrador: Optional[str] = None
    telefono: Optional[str] = None
    nombre: Optional[str] = None
    apellido_1: Optional[str] = None
    fecha_nacimiento: Optional[date] = None
    foto_perfil_url: Optional[str] = None
    foto_perfil_mime_type: Optional[str] = None


class UsuarioListResponseSchema(BaseModel):
    """Define el esquema de datos usuario list response schema para validar entradas y respuestas."""
    usuarios: list[UsuarioResponseSchema]


class UsuarioMessageResponseSchema(BaseModel):
    """Define el esquema de datos usuario message response schema para validar entradas y respuestas."""
    message: str
    usuario: UsuarioResponseSchema


class AdminUsuarioCreateSchema(BaseModel):
    """Define el esquema de datos admin usuario create schema para validar entradas y respuestas."""
    correo: str = Field(..., min_length=1, max_length=150)
    es_admin: bool = False
    es_super_admin: bool = False
    es_entrenador: bool = False
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AdminUsuarioRolUpdateSchema(BaseModel):
    """Define el esquema de datos admin usuario rol update schema para validar entradas y respuestas."""
    es_admin: Optional[bool] = None
    es_super_admin: Optional[bool] = None
    es_entrenador: Optional[bool] = None
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AdminUsuarioResponseSchema(BaseModel):
    """Define el esquema de datos admin usuario response schema para validar entradas y respuestas."""
    id_usuario: int
    correo: str
    es_admin: bool
    es_super_admin: bool
    es_entrenador: bool
    nombre: Optional[str] = None
    apellido_1: Optional[str] = None
    telefono: Optional[str] = None
    foto_perfil_url: Optional[str] = None


class AdminUsuarioListResponseSchema(BaseModel):
    """Define el esquema de datos admin usuario list response schema para validar entradas y respuestas."""
    usuarios: list[AdminUsuarioResponseSchema]


class AdminUsuarioMessageResponseSchema(BaseModel):
    """Define el esquema de datos admin usuario message response schema para validar entradas y respuestas."""
    message: str
    usuario: AdminUsuarioResponseSchema


class AdminUsuarioDeleteResponseSchema(BaseModel):
    """Define el esquema de datos admin usuario delete response schema para validar entradas y respuestas."""
    message: str
