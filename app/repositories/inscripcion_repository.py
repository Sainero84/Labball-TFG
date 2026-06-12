from sqlalchemy.orm import Session

from app.entities.inscripcion_entity import InscripcionEntity


def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(InscripcionEntity).all()


def get_by_id(db: Session, inscripcion_id: int):
    """Realiza la operacion get by id contra la base de datos."""
    return db.query(InscripcionEntity).filter(
        InscripcionEntity.id_inscripcion == inscripcion_id
    ).first()


def get_by_usuario_id(db: Session, usuario_id: int):
    """Realiza la operacion get by usuario id contra la base de datos."""
    return db.query(InscripcionEntity).filter(
        InscripcionEntity.id_usuario == usuario_id
    ).all()


def get_by_id_and_usuario_id(db: Session, inscripcion_id: int, usuario_id: int):
    """Realiza la operacion get by id and usuario id contra la base de datos."""
    return db.query(InscripcionEntity).filter(
        InscripcionEntity.id_inscripcion == inscripcion_id,
        InscripcionEntity.id_usuario == usuario_id
    ).first()


def get_by_dni(db: Session, dni: str):
    """Realiza la operacion get by dni contra la base de datos."""
    return db.query(InscripcionEntity).filter(
        InscripcionEntity.dni == dni
    ).first()


def create(db: Session, inscripcion_data: dict):
    """Realiza la operacion create contra la base de datos."""
    new_inscripcion = InscripcionEntity(
        id_usuario=inscripcion_data["id_usuario"],
        id_tarifa=inscripcion_data.get("id_tarifa"),
        numero_sesiones=inscripcion_data["numero_sesiones"],
        nombre=inscripcion_data["nombre"],
        apellidos=inscripcion_data["apellidos"],
        dni=inscripcion_data["dni"],
        fecha_nacimiento=inscripcion_data["fecha_nacimiento"],
        correo=inscripcion_data["correo"],
        telefono=inscripcion_data["telefono"],
        club=inscripcion_data["club"],
        categoria=inscripcion_data["categoria"],
        precio_sin_descuento=inscripcion_data["precio_sin_descuento"],
        descuento_aplicado=inscripcion_data["descuento_aplicado"],
        precio_final=inscripcion_data["precio_final"],
        pagado=inscripcion_data["pagado"],
        id_descuento=inscripcion_data["id_descuento"]
    )

    db.add(new_inscripcion)
    db.commit()
    db.refresh(new_inscripcion)

    return new_inscripcion


def update(db: Session, inscripcion_id: int, updated_fields: dict):
    """Realiza la operacion update contra la base de datos."""
    inscripcion = get_by_id(db, inscripcion_id)

    if inscripcion is None:
        return None

    for key, value in updated_fields.items():
        setattr(inscripcion, key, value)

    db.commit()
    db.refresh(inscripcion)

    return inscripcion


def delete(db: Session, inscripcion_id: int):
    """Realiza la operacion delete contra la base de datos."""
    inscripcion = get_by_id(db, inscripcion_id)

    if inscripcion is None:
        return None

    db.delete(inscripcion)
    db.commit()

    return inscripcion
