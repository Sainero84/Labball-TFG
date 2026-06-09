from sqlalchemy.orm import Session

from app.entities.tarifa_entity import TarifaEntity


def get_all(db: Session):
    return db.query(TarifaEntity).order_by(TarifaEntity.numero_sesiones).all()


def get_all_active(db: Session):
    return db.query(TarifaEntity).filter(
        TarifaEntity.activa == True
    ).order_by(TarifaEntity.numero_sesiones).all()


def get_by_id(db: Session, tarifa_id: int):
    return db.query(TarifaEntity).filter(
        TarifaEntity.id_tarifa == tarifa_id
    ).first()


def get_by_numero_sesiones(db: Session, numero_sesiones: int):
    return db.query(TarifaEntity).filter(
        TarifaEntity.numero_sesiones == numero_sesiones
    ).first()


def create(db: Session, tarifa_data: dict):
    tarifa = TarifaEntity(
        numero_sesiones=tarifa_data["numero_sesiones"],
        precio_total=tarifa_data["precio_total"],
        precio_por_sesion=tarifa_data["precio_por_sesion"],
        activa=tarifa_data["activa"]
    )

    db.add(tarifa)
    db.commit()
    db.refresh(tarifa)

    return tarifa


def update(db: Session, tarifa_id: int, updated_fields: dict):
    tarifa = get_by_id(db, tarifa_id)

    if tarifa is None:
        return None

    for key, value in updated_fields.items():
        setattr(tarifa, key, value)

    db.commit()
    db.refresh(tarifa)

    return tarifa


def delete(db: Session, tarifa_id: int):
    tarifa = get_by_id(db, tarifa_id)

    if tarifa is None:
        return None

    db.delete(tarifa)
    db.commit()

    return tarifa
