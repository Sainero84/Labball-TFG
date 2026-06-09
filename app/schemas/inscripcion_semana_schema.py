from pydantic import BaseModel, Field


# --------------------------------------------------
# SCHEMA DE ENTRADA: ASIGNAR UNA SEMANA A UNA INSCRIPCIÓN
# --------------------------------------------------

class InscripcionSemanaCreateSchema(BaseModel):
    id_inscripcion: int = Field(..., gt=0)
    id_semana: int = Field(..., gt=0)


# --------------------------------------------------
# SCHEMA DE SALIDA: RELACIÓN INSCRIPCIÓN-SEMANA
# --------------------------------------------------

class InscripcionSemanaResponseSchema(BaseModel):
    id_inscripcion: int
    id_semana: int


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + RELACIÓN
# --------------------------------------------------

class InscripcionSemanaMessageResponseSchema(BaseModel):
    message: str
    relacion: InscripcionSemanaResponseSchema


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE RELACIONES
# --------------------------------------------------

class InscripcionSemanaListResponseSchema(BaseModel):
    relaciones: list[InscripcionSemanaResponseSchema]