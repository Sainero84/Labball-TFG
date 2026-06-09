from pydantic import BaseModel, Field
from typing import Optional


class TarifaCreateSchema(BaseModel):
    numero_sesiones: int = Field(..., gt=0)
    precio_total: float = Field(..., gt=0)
    precio_por_sesion: Optional[float] = Field(default=None, gt=0)
    activa: bool = True


class TarifaUpdateSchema(BaseModel):
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    precio_total: Optional[float] = Field(default=None, gt=0)
    precio_por_sesion: Optional[float] = Field(default=None, gt=0)
    activa: Optional[bool] = None


class TarifaResponseSchema(BaseModel):
    id_tarifa: int
    numero_sesiones: int
    precio_total: float
    precio_por_sesion: float
    activa: bool


class TarifaListResponseSchema(BaseModel):
    tarifas: list[TarifaResponseSchema]


class TarifaMessageResponseSchema(BaseModel):
    message: str
    tarifa: TarifaResponseSchema
