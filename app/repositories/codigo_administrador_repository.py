from sqlalchemy.orm import Session

from app.entities.codigo_administrador_entity import CodigoAdministradorEntity


def get_all(db: Session):
    """Realiza la operacion get all contra la base de datos."""
    return db.query(CodigoAdministradorEntity).order_by(
        CodigoAdministradorEntity.id_codigo_administrador.desc()
    ).all()


def get_by_codigo(db: Session, codigo: str):
    """Realiza la operacion get by codigo contra la base de datos."""
    return db.query(CodigoAdministradorEntity).filter(
        CodigoAdministradorEntity.codigo == codigo
    ).first()


def create(db: Session, codigo: str):
    """Realiza la operacion create contra la base de datos."""
    codigo_administrador = CodigoAdministradorEntity(codigo=codigo)

    db.add(codigo_administrador)
    db.commit()
    db.refresh(codigo_administrador)

    return codigo_administrador
