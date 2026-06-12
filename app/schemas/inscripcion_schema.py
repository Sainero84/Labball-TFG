from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel, Field

from app.schemas.entrenamiento_schema import EntrenamientoResponseSchema


class ReservaCreateSchema(BaseModel):
    """Define el esquema de datos reserva create schema para validar entradas y respuestas."""
    id_tarifa: Optional[int] = Field(default=None, gt=0)
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    nombre: str = Field(..., min_length=1, max_length=100)
    apellidos: str = Field(..., min_length=1, max_length=150)
    dni: str = Field(..., min_length=1, max_length=20)
    fecha_nacimiento: date
    correo: str = Field(..., min_length=1, max_length=150)
    telefono: Optional[str] = Field(default=None, max_length=20)
    club: Optional[str] = Field(default=None, max_length=150)
    categoria: Optional[str] = Field(default=None, max_length=100)
    codigo_descuento: Optional[str] = Field(default=None, max_length=50)
    semanas: list[int] = Field(default_factory=list)


class ReservaPreviewRequestSchema(BaseModel):
    """Define el esquema de datos reserva preview request schema para validar entradas y respuestas."""
    id_tarifa: Optional[int] = Field(default=None, gt=0)
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    codigo_descuento: Optional[str] = Field(default=None, max_length=50)


class ReservaPreviewResponseSchema(BaseModel):
    """Define el esquema de datos reserva preview response schema para validar entradas y respuestas."""
    id_tarifa: Optional[int] = None
    numero_sesiones: int
    precio_sin_descuento: float
    descuento_aplicado: float
    precio_final: float
    codigo_descuento: Optional[str] = None


class ReservaResponseSchema(BaseModel):
    """Define el esquema de datos reserva response schema para validar entradas y respuestas."""
    id_reserva: int
    id_usuario: int
    id_jugador: Optional[int] = None
    id_tarifa: Optional[int] = None
    numero_sesiones: int
    nombre: str
    apellidos: str
    dni: str
    fecha_nacimiento: date
    correo: str = Field(..., min_length=1, max_length=150)
    telefono: Optional[str] = None
    club: Optional[str] = None
    categoria: Optional[str] = None
    codigo_descuento: Optional[str] = None
    semanas: list[int]
    precio_sin_descuento: float
    descuento_aplicado: float
    precio_final: float
    pagado: bool


class ReservaListResponseSchema(BaseModel):
    """Define el esquema de datos reserva list response schema para validar entradas y respuestas."""
    reservas: list[ReservaResponseSchema]


class ReservaMessageResponseSchema(BaseModel):
    """Define el esquema de datos reserva message response schema para validar entradas y respuestas."""
    message: str
    reserva: ReservaResponseSchema


class ReservaAdminListItemSchema(BaseModel):
    """Define el esquema de datos reserva admin list item schema para validar entradas y respuestas."""
    id_reserva: int
    nombre: str
    apellidos: str
    correo: str
    numero_sesiones: int
    pagado: bool
    entrenamientos_asignados: int
    asignacion_completa: bool
    tiene_entrenamientos: bool


class ReservaAdminListResponseSchema(BaseModel):
    """Define el esquema de datos reserva admin list response schema para validar entradas y respuestas."""
    reservas: list[ReservaAdminListItemSchema]


class ReservaPagadoUpdateSchema(BaseModel):
    """Define el esquema de datos reserva pagado update schema para validar entradas y respuestas."""
    pagado: bool


class ReservaEntrenamientoAsignarItemSchema(BaseModel):
    """Define el esquema de datos reserva entrenamiento asignar item schema para validar entradas y respuestas."""
    id_entrenador: int = Field(..., gt=0)
    id_ubicacion: int = Field(..., gt=0)
    hora_inicio: datetime
    hora_fin: datetime


class ReservaEntrenamientosAsignarSchema(BaseModel):
    """Define el esquema de datos reserva entrenamientos asignar schema para validar entradas y respuestas."""
    entrenamientos: list[ReservaEntrenamientoAsignarItemSchema]


class ReservaEntrenamientosAsignadosResponseSchema(BaseModel):
    """Define el esquema de datos reserva entrenamientos asignados response schema para validar entradas y respuestas."""
    message: str
    entrenamientos: list[EntrenamientoResponseSchema]
