from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoCreateSchema(BaseModel):

    nombre_entrenador: str = Field(..., min_length=1, max_length=150)

    ubicacion: str = Field(..., min_length=1, max_length=200)

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

    nombre_entrenador: Optional[str] = Field(
        default=None,
        min_length=1,
        max_length=150
    )

    ubicacion: Optional[str] = Field(
        default=None,
        min_length=1,
        max_length=200
    )

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

    id_entrenamiento: int

    nombre_entrenador: str

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

    entrenamientos: list[EntrenamientoResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + ENTRENAMIENTO
# --------------------------------------------------

class EntrenamientoMessageResponseSchema(BaseModel):

    message: str

    entrenamiento: EntrenamientoResponseSchema
