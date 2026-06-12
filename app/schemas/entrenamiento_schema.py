from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoCreateSchema(BaseModel):

    """Define el esquema de datos entrenamiento create schema para validar entradas y respuestas."""
    id_entrenador: int = Field(..., gt=0)

    id_ubicacion: int = Field(..., gt=0)

    hora_inicio: datetime

    hora_fin: datetime

    id_inscripcion: Optional[int] = None

    id_jugador: Optional[int] = None

    id_usuario: Optional[int] = None

    id_reserva: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoUpdateSchema(BaseModel):

    """Define el esquema de datos entrenamiento update schema para validar entradas y respuestas."""
    id_entrenador: Optional[int] = Field(default=None, gt=0)

    id_ubicacion: Optional[int] = Field(default=None, gt=0)

    hora_inicio: Optional[datetime] = None

    hora_fin: Optional[datetime] = None

    id_jugador: Optional[int] = None

    id_usuario: Optional[int] = None

    id_reserva: Optional[int] = None

    id_inscripcion: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE SALIDA: ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoResponseSchema(BaseModel):

    """Define el esquema de datos entrenamiento response schema para validar entradas y respuestas."""
    id_entrenamiento: int

    id_entrenador: int

    nombre_entrenador: str

    id_ubicacion: int

    ubicacion: str

    hora_inicio: datetime

    hora_fin: datetime

    id_inscripcion: Optional[int] = None

    id_jugador: Optional[int] = None

    id_usuario: Optional[int] = None

    id_reserva: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE ENTRENAMIENTOS
# --------------------------------------------------

class EntrenamientoListResponseSchema(BaseModel):

    """Define el esquema de datos entrenamiento list response schema para validar entradas y respuestas."""
    entrenamientos: list[EntrenamientoResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoMessageResponseSchema(BaseModel):

    """Define el esquema de datos entrenamiento message response schema para validar entradas y respuestas."""
    message: str

    entrenamiento: EntrenamientoResponseSchema
