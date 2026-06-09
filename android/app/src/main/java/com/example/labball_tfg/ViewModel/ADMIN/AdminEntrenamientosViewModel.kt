package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.EntrenamientoCreateRequest
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import com.example.labball_tfg.Modelo.EntrenamientoUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminEntrenamientosViewModel : ViewModel() {

    private val _entrenamientoGuardado = MutableStateFlow<EntrenamientoResponse?>(null)
    val entrenamientoGuardado: StateFlow<EntrenamientoResponse?> =
        _entrenamientoGuardado.asStateFlow()

    private val _entrenamientoEliminado = MutableStateFlow(false)
    val entrenamientoEliminado: StateFlow<Boolean> = _entrenamientoEliminado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun crearEntrenamiento(
        token: String,
        entrenamiento: EntrenamientoCreateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _entrenamientoGuardado.value = null
            _entrenamientoEliminado.value = false

            try {
                val response = API.apiDao.createEntrenamiento(
                    token = "Bearer $token",
                    request = entrenamiento
                )

                if (response.isSuccessful) {
                    _entrenamientoGuardado.value = response.body()?.entrenamiento
                } else {
                    _errorMessage.value =
                        "Error al crear entrenamiento: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarEntrenamiento(
        token: String,
        idEntrenamiento: Int,
        entrenamiento: EntrenamientoUpdateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _entrenamientoGuardado.value = null
            _entrenamientoEliminado.value = false

            try {
                val response = API.apiDao.updateEntrenamiento(
                    token = "Bearer $token",
                    idEntrenamiento = idEntrenamiento,
                    request = entrenamiento
                )

                if (response.isSuccessful) {
                    _entrenamientoGuardado.value = response.body()?.entrenamiento
                } else {
                    _errorMessage.value =
                        "Error al actualizar entrenamiento: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarEntrenamiento(
        token: String,
        idEntrenamiento: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _entrenamientoGuardado.value = null
            _entrenamientoEliminado.value = false

            try {
                val response = API.apiDao.deleteEntrenamiento(
                    token = "Bearer $token",
                    idEntrenamiento = idEntrenamiento
                )

                if (response.isSuccessful) {
                    _entrenamientoEliminado.value = true
                } else {
                    _errorMessage.value =
                        "Error al eliminar entrenamiento: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarEstado() {
        _entrenamientoGuardado.value = null
        _entrenamientoEliminado.value = false
        _errorMessage.value = null
    }
}
