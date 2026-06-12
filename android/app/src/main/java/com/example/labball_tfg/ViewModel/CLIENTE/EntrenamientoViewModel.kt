package com.example.labball_tfg.ViewModel.CLIENTE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Mantiene el estado y las operaciones de entrenamiento view model para la interfaz.
class EntrenamientoViewModel : ViewModel() {

    private val _entrenamientos = MutableStateFlow<List<EntrenamientoResponse>>(emptyList())
    val entrenamientos: StateFlow<List<EntrenamientoResponse>> = _entrenamientos.asStateFlow()

    private val _entrenamientoSeleccionado = MutableStateFlow<EntrenamientoResponse?>(null)
    val entrenamientoSeleccionado: StateFlow<EntrenamientoResponse?> =
        _entrenamientoSeleccionado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Carga cargar mis entrenamientos desde la API y actualiza el estado asociado.
    fun cargarMisEntrenamientos(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getMisEntrenamientos("Bearer $token")

                if (response.isSuccessful) {
                    _entrenamientos.value =
                        response.body()?.entrenamientos ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar entrenamientos: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga cargar entrenamiento por id desde la API y actualiza el estado asociado.
    fun cargarEntrenamientoPorId(
        token: String,
        idEntrenamiento: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getMiEntrenamientoById(
                    token = "Bearer $token",
                    idEntrenamiento = idEntrenamiento
                )

                if (response.isSuccessful) {
                    _entrenamientoSeleccionado.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar entrenamiento: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpia limpiar entrenamiento seleccionado para reiniciar el estado temporal.
    fun limpiarEntrenamientoSeleccionado() {
        _entrenamientoSeleccionado.value = null
    }

    // Limpia limpiar error para reiniciar el estado temporal.
    fun limpiarError() {
        _errorMessage.value = null
    }
}