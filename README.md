# Labball Backend

API REST para Labball construida con FastAPI, SQLAlchemy, MySQL y Firebase Authentication.

## Requisitos

- Python 3.13
- MySQL
- FFmpeg
- Firebase Admin SDK

## Instalacion

```powershell
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Configuracion

El archivo de credenciales de Firebase debe colocarse en:

```text
app/firebase/serviceAccountKey.json
```

Este archivo no se sube a GitHub por seguridad.

Variables de entorno utiles:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="12345"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="tfg_db"
$env:PUBLIC_BASE_URL="http://127.0.0.1:8000"
$env:FFMPEG_PATH="C:\ffmpeg\bin\ffmpeg.exe"
$env:FIREBASE_WEB_API_KEY="TU_WEB_API_KEY"
```

## Ejecutar API

Local:

```powershell
python -m uvicorn app.main:app --reload
```

Para movil fisico en la misma red:

```powershell
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Documentacion:

```text
http://127.0.0.1:8000/docs
```

## Base de datos

Las migraciones SQL estan en la carpeta `migrations/` y deben aplicarse en orden sobre la base de datos `tfg_db`.
