from pydantic import BaseModel, Field


class EntrenadorResponseSchema(BaseModel):
    """Define el esquema de datos entrenador response schema para validar entradas y respuestas."""
    id_entrenador: int
    nombre: str
    activo: bool


class EntrenadorListResponseSchema(BaseModel):
    """Define el esquema de datos entrenador list response schema para validar entradas y respuestas."""
    entrenadores: list[EntrenadorResponseSchema]


class UbicacionCreateSchema(BaseModel):
    """Define el esquema de datos ubicacion create schema para validar entradas y respuestas."""
    nombre: str = Field(..., min_length=1, max_length=200)


class UbicacionResponseSchema(BaseModel):
    """Define el esquema de datos ubicacion response schema para validar entradas y respuestas."""
    id_ubicacion: int
    nombre: str
    activo: bool


class UbicacionListResponseSchema(BaseModel):
    """Define el esquema de datos ubicacion list response schema para validar entradas y respuestas."""
    ubicaciones: list[UbicacionResponseSchema]


class UbicacionMessageResponseSchema(BaseModel):
    """Define el esquema de datos ubicacion message response schema para validar entradas y respuestas."""
    message: str
    ubicacion: UbicacionResponseSchema


class UbicacionDeleteResponseSchema(BaseModel):
    """Define el esquema de datos ubicacion delete response schema para validar entradas y respuestas."""
    message: str
