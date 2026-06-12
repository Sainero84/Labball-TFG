from firebase_admin import auth
from fastapi import HTTPException


def verify_firebase_token(token: str):
    """Gestiona la integracion con Firebase para verify firebase token."""
    try:
        decoded_token = auth.verify_id_token(token)
        return decoded_token

    except Exception:
        raise HTTPException(
            status_code=401,
            detail="Token de Firebase inválido"
        )