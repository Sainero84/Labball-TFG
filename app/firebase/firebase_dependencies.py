from fastapi import Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session

from app.database.connection import get_db
from app.firebase.firebase_auth import verify_firebase_token
from app.repositories import usuario_repository


security = HTTPBearer()


def get_current_firebase_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    token = credentials.credentials

    if not token:
        raise HTTPException(
            status_code=401,
            detail="Token no proporcionado"
        )

    return verify_firebase_token(token)


def get_current_user(
    firebase_user: dict = Depends(get_current_firebase_user),
    db: Session = Depends(get_db)
):
    firebase_uid = firebase_user.get("uid")

    if not firebase_uid:
        raise HTTPException(
            status_code=401,
            detail="Token de Firebase sin UID"
        )

    user = usuario_repository.get_by_firebase_uid(db, firebase_uid)

    if user is None:
        raise HTTPException(
            status_code=404,
            detail="Usuario no registrado en MySQL"
        )

    return user


def get_current_admin_user(current_user=Depends(get_current_user)):
    if not current_user.es_admin and not current_user.es_super_admin:
        raise HTTPException(
            status_code=403,
            detail="Permisos de administrador requeridos"
        )

    return current_user


def get_current_super_admin_user(current_user=Depends(get_current_user)):
    if not current_user.es_super_admin:
        raise HTTPException(
            status_code=403,
            detail="Permisos de superadministrador requeridos"
        )

    return current_user
