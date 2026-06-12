import firebase_admin
from firebase_admin import credentials


# Ruta al JSON de la cuenta de servicio
SERVICE_ACCOUNT_PATH = "app/firebase/serviceAccountKey.json"


def initialize_firebase():
    # Evitamos inicializar Firebase más de una vez
    """Gestiona la integracion con Firebase para initialize firebase."""
    if not firebase_admin._apps:
        cred = credentials.Certificate(SERVICE_ACCOUNT_PATH)
        firebase_admin.initialize_app(cred)