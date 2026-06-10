from pydantic import BaseModel, Field
from typing import Optional


class DescuentoAdminCreateSchema(BaseModel):
    porcentaje: float = Field(..., gt=0, le=100)
    codigo: Optional[str] = Field(default=None, min_length=1, max_length=50)


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR DESCUENTO
# --------------------------------------------------

class DescuentoUpdateSchema(BaseModel):
    codigo: Optional[str] = Field(default=None, min_length=1, max_length=50)
    porcentaje: Optional[float] = Field(default=None, gt=0, le=100)


# --------------------------------------------------
# SCHEMA DE SALIDA: DESCUENTO
# --------------------------------------------------

class DescuentoResponseSchema(BaseModel):
    id_descuento: int
    codigo: str
    porcentaje: float


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE DESCUENTOS
# --------------------------------------------------

class DescuentoListResponseSchema(BaseModel):
    descuentos: list[DescuentoResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + DESCUENTO
# --------------------------------------------------

class DescuentoMessageResponseSchema(BaseModel):
    message: str
    descuento: DescuentoResponseSchema


class DescuentoDeleteResponseSchema(BaseModel):
    message: str
