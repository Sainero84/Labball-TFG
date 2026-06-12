from pydantic import BaseModel, Field


class CodigoAdministradorGenerateSchema(BaseModel):
    """Define el esquema de datos codigo administrador generate schema para validar entradas y respuestas."""
    cantidad: int = Field(default=1, ge=1, le=50)


class CodigoAdministradorValidateSchema(BaseModel):
    """Define el esquema de datos codigo administrador validate schema para validar entradas y respuestas."""
    codigo: str = Field(..., min_length=1, max_length=64)


class CodigoAdministradorResponseSchema(BaseModel):
    """Define el esquema de datos codigo administrador response schema para validar entradas y respuestas."""
    id_codigo_administrador: int
    codigo: str


class CodigoAdministradorListResponseSchema(BaseModel):
    """Define el esquema de datos codigo administrador list response schema para validar entradas y respuestas."""
    codigos: list[CodigoAdministradorResponseSchema]


class CodigoAdministradorGenerateResponseSchema(BaseModel):
    """Define el esquema de datos codigo administrador generate response schema para validar entradas y respuestas."""
    message: str
    codigos: list[CodigoAdministradorResponseSchema]


class CodigoAdministradorValidateResponseSchema(BaseModel):
    """Define el esquema de datos codigo administrador validate response schema para validar entradas y respuestas."""
    codigo: str
    valido: bool
    message: str
