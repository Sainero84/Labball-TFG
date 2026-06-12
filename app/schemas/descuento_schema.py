from pydantic import BaseModel, Field
from typing import Optional


class DescuentoAdminCreateSchema(BaseModel):
    """Define el esquema de datos descuento admin create schema para validar entradas y respuestas."""
    porcentaje: float = Field(..., gt=0, le=100)
    codigo: Optional[str] = Field(default=None, min_length=1, max_length=50)


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR DESCUENTO
# --------------------------------------------------

class DescuentoUpdateSchema(BaseModel):
    """Define el esquema de datos descuento update schema para validar entradas y respuestas."""
    codigo: Optional[str] = Field(default=None, min_length=1, max_length=50)
    porcentaje: Optional[float] = Field(default=None, gt=0, le=100)


# --------------------------------------------------
# SCHEMA DE SALIDA: DESCUENTO
# --------------------------------------------------

class DescuentoResponseSchema(BaseModel):
    """Define el esquema de datos descuento response schema para validar entradas y respuestas."""
    id_descuento: int
    codigo: str
    porcentaje: float


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE DESCUENTOS
# --------------------------------------------------

class DescuentoListResponseSchema(BaseModel):
    """Define el esquema de datos descuento list response schema para validar entradas y respuestas."""
    descuentos: list[DescuentoResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + DESCUENTO
# --------------------------------------------------

class DescuentoMessageResponseSchema(BaseModel):
    """Define el esquema de datos descuento message response schema para validar entradas y respuestas."""
    message: str
    descuento: DescuentoResponseSchema


class DescuentoDeleteResponseSchema(BaseModel):
    """Define el esquema de datos descuento delete response schema para validar entradas y respuestas."""
    message: str
