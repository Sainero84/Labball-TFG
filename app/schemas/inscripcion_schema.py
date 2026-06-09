from datetime import date, datetime
from pydantic import BaseModel, Field
from typing import Optional

from app.schemas.entrenamiento_schema import EntrenamientoResponseSchema


# --------------------------------------------------
# SCHEMA DE ENTRADA: CREAR INSCRIPCIÓN
# --------------------------------------------------

class InscripcionCreateSchema(BaseModel):
    id_usuario: int = Field(..., gt=0)
    id_tarifa: Optional[int] = Field(default=None, gt=0)
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    nombre: str = Field(..., min_length=1, max_length=100)
    apellidos: str = Field(..., min_length=1, max_length=150)
    dni: str = Field(..., min_length=1, max_length=20)
    fecha_nacimiento: date
    correo: str
    telefono: Optional[str] = Field(default=None, max_length=20)
    club: Optional[str] = Field(default=None, max_length=150)
    categoria: Optional[str] = Field(default=None, max_length=100)
    precio_sin_descuento: Optional[float] = Field(default=None, gt=0)
    descuento_aplicado: float = Field(default=0, ge=0)
    pagado: bool = False
    id_descuento: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE ENTRADA: ACTUALIZAR INSCRIPCIÓN
# --------------------------------------------------

class InscripcionUpdateSchema(BaseModel):
    id_usuario: Optional[int] = Field(default=None, gt=0)
    id_tarifa: Optional[int] = Field(default=None, gt=0)
    numero_sesiones: Optional[int] = Field(default=None, gt=0)
    nombre: Optional[str] = Field(default=None, min_length=1, max_length=100)
    apellidos: Optional[str] = Field(default=None, min_length=1, max_length=150)
    dni: Optional[str] = Field(default=None, min_length=1, max_length=20)
    fecha_nacimiento: Optional[date] = None
    correo: Optional[str] = Field(default=None, min_length=1, max_length=150)
    telefono: Optional[str] = Field(default=None, max_length=20)
    club: Optional[str] = Field(default=None, max_length=150)
    categoria: Optional[str] = Field(default=None, max_length=100)
    precio_sin_descuento: Optional[float] = Field(default=None, gt=0)
    descuento_aplicado: Optional[float] = Field(default=None, ge=0)
    pagado: Optional[bool] = None
    id_descuento: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE SALIDA: INSCRIPCIÓN
# --------------------------------------------------

class InscripcionResponseSchema(BaseModel):
    id_inscripcion: int
    id_usuario: int
    id_tarifa: Optional[int] = None
    numero_sesiones: int
    nombre: str
    apellidos: str
    dni: str
    fecha_nacimiento: date
    correo: str
    telefono: Optional[str] = None
    club: Optional[str] = None
    categoria: Optional[str] = None
    precio_sin_descuento: float
    descuento_aplicado: float
    precio_final: float
    pagado: bool
    id_descuento: Optional[int] = None


# --------------------------------------------------
# SCHEMA DE SALIDA: LISTA DE INSCRIPCIONES
# --------------------------------------------------

class InscripcionListResponseSchema(BaseModel):
    inscripciones: list[InscripcionResponseSchema]


# --------------------------------------------------
# SCHEMA DE SALIDA: MENSAJE + INSCRIPCIÓN
# --------------------------------------------------

class InscripcionMessageResponseSchema(BaseModel):
    message: str
    inscripcion: InscripcionResponseSchema


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
    nombre_entrenador: str = Field(..., min_length=1, max_length=150)
    ubicacion: str = Field(..., min_length=1, max_length=200)
    hora_inicio: datetime
    hora_fin: datetime


class ReservaEntrenamientosAsignarSchema(BaseModel):
    entrenamientos: list[ReservaEntrenamientoAsignarItemSchema]


class ReservaEntrenamientosAsignadosResponseSchema(BaseModel):
    message: str
    entrenamientos: list[EntrenamientoResponseSchema]
