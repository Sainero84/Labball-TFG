from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel, Field

from app.schemas.entrenamiento_schema import EntrenamientoResponseSchema


class ReservaCreateSchema(BaseModel):
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
    id_tarifa: Optional[int] = Field(default=None, gt=0)
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    codigo_descuento: Optional[str] = Field(default=None, max_length=50)


class ReservaPreviewResponseSchema(BaseModel):
    id_tarifa: Optional[int] = None
    numero_sesiones: int
    precio_sin_descuento: float
    descuento_aplicado: float
    precio_final: float
    codigo_descuento: Optional[str] = None


class ReservaResponseSchema(BaseModel):
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
    reservas: list[ReservaResponseSchema]


class ReservaMessageResponseSchema(BaseModel):
    message: str
    reserva: ReservaResponseSchema


class ReservaAdminListItemSchema(BaseModel):
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
    reservas: list[ReservaAdminListItemSchema]


class ReservaPagadoUpdateSchema(BaseModel):
    pagado: bool


class ReservaEntrenamientoAsignarItemSchema(BaseModel):
    id_entrenador: int = Field(..., gt=0)
    id_ubicacion: int = Field(..., gt=0)
    hora_inicio: datetime
    hora_fin: datetime


class ReservaEntrenamientosAsignarSchema(BaseModel):
    entrenamientos: list[ReservaEntrenamientoAsignarItemSchema]


class ReservaEntrenamientosAsignadosResponseSchema(BaseModel):
    message: str
    entrenamientos: list[EntrenamientoResponseSchema]
