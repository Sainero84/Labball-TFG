from pydantic import BaseModel, Field, field_validator
from datetime import date
from typing import Optional


class UsuarioCreateSchema(BaseModel):
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
        if value == "":
            return None

        return value


class UsuarioUpdateSchema(BaseModel):
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
        if value == "":
            return None

        return value


class UsuarioMeResponseSchema(BaseModel):
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
    telefono: Optional[str] = Field(default=None, max_length=20)


class UsuarioPerfilUpdateSchema(BaseModel):
    nombre: Optional[str] = Field(default=None, max_length=100)
    apellido_1: Optional[str] = Field(default=None, max_length=150)
    fecha_nacimiento: Optional[date] = None
    telefono: Optional[str] = Field(default=None, max_length=20)

    @field_validator("fecha_nacimiento", mode="before")
    @classmethod
    def empty_fecha_nacimiento_to_none(cls, value):
        if value == "":
            return None

        return value


class UsuarioFotoPerfilUpdateSchema(BaseModel):
    foto_perfil_url: str = Field(..., min_length=1, max_length=500)
    foto_perfil_mime_type: Optional[str] = Field(default=None, max_length=100)


class UsuarioResponseSchema(BaseModel):
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
    usuarios: list[UsuarioResponseSchema]


class UsuarioMessageResponseSchema(BaseModel):
    message: str
    usuario: UsuarioResponseSchema


class AdminUsuarioCreateSchema(BaseModel):
    correo: str = Field(..., min_length=1, max_length=150)
    es_admin: bool = False
    es_super_admin: bool = False
    es_entrenador: bool = False
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AdminUsuarioRolUpdateSchema(BaseModel):
    es_admin: Optional[bool] = None
    es_super_admin: Optional[bool] = None
    es_entrenador: Optional[bool] = None
    codigo_administrador: Optional[str] = Field(default=None, max_length=64)


class AdminUsuarioResponseSchema(BaseModel):
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
    usuarios: list[AdminUsuarioResponseSchema]


class AdminUsuarioMessageResponseSchema(BaseModel):
    message: str
    usuario: AdminUsuarioResponseSchema


class AdminUsuarioDeleteResponseSchema(BaseModel):
    message: str
