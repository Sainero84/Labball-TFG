from pydantic import BaseModel, Field
from typing import Optional


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR JUGADOR
# --------------------------------------------------

class JugadorCreateSchema(BaseModel):
    """Define el esquema de datos jugador create schema para validar entradas y respuestas."""
    id_usuario: int = Field(..., gt=0)
    nombre: str = Field(..., min_length=1, max_length=100)
    apellidos: str = Field(..., min_length=1, max_length=150)
    peso: Optional[float] = Field(default=None, gt=0, le=300)
    altura: Optional[float] = Field(default=None, gt=0, le=3)
    posicion: Optional[str] = Field(default=None, max_length=50)
    tiro: Optional[int] = Field(default=None, ge=0, le=100)
    fisico: Optional[int] = Field(default=None, ge=0, le=100)
    bote: Optional[int] = Field(default=None, ge=0, le=100)
    pase: Optional[int] = Field(default=None, ge=0, le=100)
    defensa: Optional[int] = Field(default=None, ge=0, le=100)
    velocidad: Optional[int] = Field(default=None, ge=0, le=100)


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR JUGADOR
# --------------------------------------------------

class JugadorUpdateSchema(BaseModel):
    """Define el esquema de datos jugador update schema para validar entradas y respuestas."""
    id_usuario: Optional[int] = Field(default=None, gt=0)
    nombre: Optional[str] = Field(default=None, min_length=1, max_length=100)
    apellidos: Optional[str] = Field(default=None, min_length=1, max_length=150)
    peso: Optional[float] = Field(default=None, gt=0, le=300)
    altura: Optional[float] = Field(default=None, gt=0, le=3)
    posicion: Optional[str] = Field(default=None, max_length=50)
    tiro: Optional[int] = Field(default=None, ge=0, le=100)
    fisico: Optional[int] = Field(default=None, ge=0, le=100)
    bote: Optional[int] = Field(default=None, ge=0, le=100)
    pase: Optional[int] = Field(default=None, ge=0, le=100)
    defensa: Optional[int] = Field(default=None, ge=0, le=100)
    velocidad: Optional[int] = Field(default=None, ge=0, le=100)


# --------------------------------------------------
# SCHEMA DE SALIDA: JUGADOR
# --------------------------------------------------

class JugadorEntrenadorAsignadoSchema(BaseModel):
    """Define el esquema de datos jugador entrenador asignado schema para validar entradas y respuestas."""
    id_entrenador: int
    nombre: str


class JugadorUbicacionAsignadaSchema(BaseModel):
    """Define el esquema de datos jugador ubicacion asignada schema para validar entradas y respuestas."""
    id_ubicacion: int
    nombre: str


class JugadorResponseSchema(BaseModel):
    """Define el esquema de datos jugador response schema para validar entradas y respuestas."""
    id_jugador: int
    id_usuario: int
    nombre: str
    apellidos: str
    peso: Optional[float] = None
    altura: Optional[float] = None
    posicion: Optional[str] = None
    tiro: Optional[int] = None
    fisico: Optional[int] = None
    bote: Optional[int] = None
    pase: Optional[int] = None
    defensa: Optional[int] = None
    velocidad: Optional[int] = None
    foto_perfil_url: Optional[str] = None
    id_entrenador: Optional[int] = None
    nombre_entrenador: Optional[str] = None
    id_ubicacion: Optional[int] = None
    ubicacion: Optional[str] = None
    entrenadores: list[JugadorEntrenadorAsignadoSchema] = Field(default_factory=list)
    ubicaciones: list[JugadorUbicacionAsignadaSchema] = Field(default_factory=list)


class JugadorEstadisticasUpdateSchema(BaseModel):
    """Define el esquema de datos jugador estadisticas update schema para validar entradas y respuestas."""
    tiro: Optional[int] = Field(default=None, ge=0, le=100)
    fisico: Optional[int] = Field(default=None, ge=0, le=100)
    bote: Optional[int] = Field(default=None, ge=0, le=100)
    pase: Optional[int] = Field(default=None, ge=0, le=100)
    defensa: Optional[int] = Field(default=None, ge=0, le=100)
    velocidad: Optional[int] = Field(default=None, ge=0, le=100)


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE JUGADORES
# --------------------------------------------------

class JugadorListResponseSchema(BaseModel):
    """Define el esquema de datos jugador list response schema para validar entradas y respuestas."""
    jugadores: list[JugadorResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + JUGADOR
# --------------------------------------------------

class JugadorMessageResponseSchema(BaseModel):
    """Define el esquema de datos jugador message response schema para validar entradas y respuestas."""
    message: str
    jugador: JugadorResponseSchema
