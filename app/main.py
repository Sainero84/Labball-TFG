from pathlib import Path

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles

from app.database.connection import engine
from app.database.connection import SessionLocal
from app.database.base import Base

from app.entities.usuario_entity import UsuarioEntity
from app.entities.jugador_entity import JugadorEntity
from app.entities.media_entity import MediaEntity
from app.entities.descuento_entity import DescuentoEntity
from app.entities.inscripcion_entity import InscripcionEntity
from app.entities.semana_entity import SemanaEntity
from app.entities.inscripcion_semana_entity import InscripcionSemanaEntity
from app.entities.tarifa_entity import TarifaEntity
from app.entities.codigo_administrador_entity import CodigoAdministradorEntity
from app.entities.entrenador_entity import EntrenadorEntity
from app.entities.ubicacion_entity import UbicacionEntity

from app.routes.usuario_routes import router as usuario_router
from app.routes.media_routes import router as media_router
from app.routes.jugador_routes import router as jugador_router
from app.routes.entrenamiento_routes import router as entrenamiento_router
from app.routes.auth_routes import router as auth_router
from app.routes.reserva_routes import router as reserva_router
from app.routes.admin_routes import router as admin_router
from app.routes.tarifa_routes import router as tarifa_router
from app.firebase.firebase_admin_config import initialize_firebase
from fastapi import Depends
from app.firebase.firebase_dependencies import get_current_firebase_user
from app.services.tarifa_service import seed_default_tarifas

BASE_DIR = Path(__file__).resolve().parent.parent
STATIC_DIR = BASE_DIR / "static"
STATIC_DIR.mkdir(exist_ok=True)

app = FastAPI()
app.mount("/static", StaticFiles(directory=str(STATIC_DIR)), name="static")

@app.on_event("startup")
def on_startup():
    initialize_firebase()
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()
    try:
        seed_default_tarifas(db)
    finally:
        db.close()


@app.get("/")
def root():
    return {"message": "API funcionando correctamente"}


@app.get("/me")
def get_me(current_user: dict = Depends(get_current_firebase_user)):
    return {
        "message": "Token válido",
        "firebase_user": current_user
    }


app.include_router(usuario_router)
app.include_router(auth_router)
app.include_router(media_router)
app.include_router(jugador_router)
app.include_router(entrenamiento_router)
app.include_router(reserva_router)
app.include_router(tarifa_router)
app.include_router(admin_router)
