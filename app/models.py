# Importamos BaseModel para definir modelos de datos
from pydantic import BaseModel

# Optional permite que un campo pueda ser opcional
from typing import Optional


# Este modelo se usará cuando el cliente quiera crear una tarea nueva
class TaskCreate(BaseModel):
    title: str
    completed: bool = False
    description: Optional[str] = None


# Este modelo se usará cuando el cliente quiera actualizar una tarea
# Todos los campos son opcionales porque quizá solo quiera modificar uno
class TaskUpdate(BaseModel):
    title: Optional[str] = None
    completed: Optional[bool] = None
    description: Optional[str] = None