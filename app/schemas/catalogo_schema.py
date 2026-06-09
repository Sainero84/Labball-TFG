from pydantic import BaseModel, Field


class EntrenadorCreateSchema(BaseModel):
    nombre: str = Field(..., min_length=1, max_length=150)


class EntrenadorResponseSchema(BaseModel):
    id_entrenador: int
    nombre: str
    activo: bool


class EntrenadorListResponseSchema(BaseModel):
    entrenadores: list[EntrenadorResponseSchema]


class EntrenadorMessageResponseSchema(BaseModel):
    message: str
    entrenador: EntrenadorResponseSchema


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
