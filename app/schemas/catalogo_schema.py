from pydantic import BaseModel, Field


class EntrenadorResponseSchema(BaseModel):
    id_entrenador: int
    nombre: str
    activo: bool


class EntrenadorListResponseSchema(BaseModel):
    entrenadores: list[EntrenadorResponseSchema]


class UbicacionCreateSchema(BaseModel):
    nombre: str = Field(..., min_length=1, max_length=200)


class UbicacionResponseSchema(BaseModel):
    id_ubicacion: int
    nombre: str
    activo: bool


class UbicacionListResponseSchema(BaseModel):
    ubicaciones: list[UbicacionResponseSchema]


class UbicacionMessageResponseSchema(BaseModel):
    message: str
    ubicacion: UbicacionResponseSchema


class UbicacionDeleteResponseSchema(BaseModel):
    message: str
