from pydantic import BaseModel, Field


class CodigoAdministradorGenerateSchema(BaseModel):
    cantidad: int = Field(default=1, ge=1, le=50)


class CodigoAdministradorValidateSchema(BaseModel):
    codigo: str = Field(..., min_length=1, max_length=64)


class CodigoAdministradorResponseSchema(BaseModel):
    id_codigo_administrador: int
    codigo: str


class CodigoAdministradorListResponseSchema(BaseModel):
    codigos: list[CodigoAdministradorResponseSchema]


class CodigoAdministradorGenerateResponseSchema(BaseModel):
    message: str
    codigos: list[CodigoAdministradorResponseSchema]


class CodigoAdministradorValidateResponseSchema(BaseModel):
    codigo: str
    valido: bool
    message: str
