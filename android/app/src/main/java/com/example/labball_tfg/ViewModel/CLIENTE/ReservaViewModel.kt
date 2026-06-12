package com.example.labball_tfg.ViewModel.CLIENTE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.ReservaCreateRequest
import com.example.labball_tfg.Modelo.ReservaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.labball_tfg.Modelo.ReservaPreviewRequest
import com.example.labball_tfg.Modelo.ReservaPreviewResponse

// Mantiene el estado y las operaciones de reserva view model para la interfaz.
class ReservaViewModel : ViewModel() {

    private val _reservas = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reservas: StateFlow<List<ReservaResponse>> = _reservas.asStateFlow()

    private val _reservaSeleccionada = MutableStateFlow<ReservaResponse?>(null)
    val reservaSeleccionada: StateFlow<ReservaResponse?> = _reservaSeleccionada.asStateFlow()

    private val _reservaPreview = MutableStateFlow<ReservaPreviewResponse?>(null)
    val reservaPreview: StateFlow<ReservaPreviewResponse?> = _reservaPreview.asStateFlow()

    private val _reservaCreada = MutableStateFlow<ReservaResponse?>(null)
    val reservaCreada: StateFlow<ReservaResponse?> = _reservaCreada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Encapsula la operacion preview reserva usada por la pantalla o el estado.
    fun previewReserva(
        token: String,
        numeroSesiones: Int,
        codigoDescuento: String?
    ) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                val response = API.apiDao.previewReserva(
                    token = "Bearer $token",
                    request = ReservaPreviewRequest(
                        numeroSesiones = numeroSesiones,
                        codigoDescuento = codigoDescuento?.ifBlank { null }
                    )
                )

                if (response.isSuccessful) {
                    _reservaPreview.value = response.body()
                } else {
                    _reservaPreview.value = null
                    _errorMessage.value =
                        "Error al calcular precio: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _reservaPreview.value = null
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    // Carga cargar mis reservas desde la API y actualiza el estado asociado.
    fun cargarMisReservas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getMisReservas("Bearer $token")

                if (response.isSuccessful) {
                    _reservas.value = response.body()?.reservas ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar reservas: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga cargar reserva por id desde la API y actualiza el estado asociado.
    fun cargarReservaPorId(
        token: String,
        idReserva: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getMiReservaById(
                    token = "Bearer $token",
                    idReserva = idReserva
                )

                if (response.isSuccessful) {
                    _reservaSeleccionada.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar reserva: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crea crear reserva mediante la API y comunica el resultado.
    fun crearReserva(
        token: String,
        reserva: ReservaCreateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _reservaCreada.value = null

            try {
                val response = API.apiDao.createReserva(
                    token = "Bearer $token",
                    request = reserva
                )

                if (response.isSuccessful) {
                    _reservaCreada.value = response.body()?.reserva
                    cargarMisReservas(token)
                } else {
                    _errorMessage.value =
                        "Error al crear reserva: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpia limpiar reserva creada para reiniciar el estado temporal.
    fun limpiarReservaCreada() {
        _reservaCreada.value = null
    }

    // Limpia limpiar reserva seleccionada para reiniciar el estado temporal.
    fun limpiarReservaSeleccionada() {
        _reservaSeleccionada.value = null
    }

    // Limpia limpiar error para reiniciar el estado temporal.
    fun limpiarError() {
        _errorMessage.value = null
    }
}