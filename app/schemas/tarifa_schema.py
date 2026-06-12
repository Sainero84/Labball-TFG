from pydantic import BaseModel, Field
from typing import Optional


class TarifaCreateSchema(BaseModel):
    """Define el esquema de datos tarifa create schema para validar entradas y respuestas."""
    numero_sesiones: int = Field(..., gt=0)
    precio_total: float = Field(..., gt=0)
    precio_por_sesion: Optional[float] = Field(default=None, gt=0)
    activa: bool = True


class TarifaUpdateSchema(BaseModel):
    """Define el esquema de datos tarifa update schema para validar entradas y respuestas."""
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    precio_total: Optional[float] = Field(default=None, gt=0)
    precio_por_sesion: Optional[float] = Field(default=None, gt=0)
    activa: Optional[bool] = None


class TarifaResponseSchema(BaseModel):
    """Define el esquema de datos tarifa response schema para validar entradas y respuestas."""
    id_tarifa: int
    numero_sesiones: int
    precio_total: float
    precio_por_sesion: float
    activa: bool


class TarifaListResponseSchema(BaseModel):
    """Define el esquema de datos tarifa list response schema para validar entradas y respuestas."""
    tarifas: list[TarifaResponseSchema]


class TarifaMessageResponseSchema(BaseModel):
    """Define el esquema de datos tarifa message response schema para validar entradas y respuestas."""
    message: str
    tarifa: TarifaResponseSchema
