from pydantic import BaseModel, Field
from typing import Optional


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR SEMANA
# --------------------------------------------------

class SemanaCreateSchema(BaseModel):
    semana: str = Field(..., min_length=1, max_length=100)
    horario: str = Field(..., pattern="^(AM|PM)$")


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR SEMANA
# --------------------------------------------------

class SemanaUpdateSchema(BaseModel):
    semana: Optional[str] = Field(default=None, min_length=1, max_length=100)
    horario: Optional[str] = Field(default=None, pattern="^(AM|PM)$")


# --------------------------------------------------
# SCHEMA DE SALIDA: SEMANA
# --------------------------------------------------

class SemanaResponseSchema(BaseModel):
    id_semana: int
    semana: str
    horario: str


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE SEMANAS
# --------------------------------------------------

class SemanaListResponseSchema(BaseModel):
    semanas: list[SemanaResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + SEMANA
# --------------------------------------------------

class SemanaMessageResponseSchema(BaseModel):
    message: str
    semana: SemanaResponseSchema