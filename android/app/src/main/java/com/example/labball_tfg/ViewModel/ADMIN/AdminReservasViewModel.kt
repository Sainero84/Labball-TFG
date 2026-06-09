package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.EntrenamientoAsignacionRequest
import com.example.labball_tfg.Modelo.EntrenamientoCreateRequest
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import com.example.labball_tfg.Modelo.ReservaAdminListItemResponse
import com.example.labball_tfg.Modelo.ReservaEntrenamientosAsignarRequest
import com.example.labball_tfg.Modelo.ReservaPagadoUpdateRequest
import com.example.labball_tfg.Modelo.ReservaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminReservasViewModel : ViewModel() {

    private val _reservas = MutableStateFlow<List<ReservaAdminListItemResponse>>(emptyList())
    val reservas: StateFlow<List<ReservaAdminListItemResponse>> = _reservas.asStateFlow()

    private val _entrenadores = MutableStateFlow<List<String>>(emptyList())
    val entrenadores: StateFlow<List<String>> = _entrenadores.asStateFlow()

    private val _ubicaciones = MutableStateFlow<List<String>>(emptyList())
    val ubicaciones: StateFlow<List<String>> = _ubicaciones.asStateFlow()

    private val _reservaSeleccionada = MutableStateFlow<ReservaResponse?>(null)
    val reservaSeleccionada: StateFlow<ReservaResponse?> =
        _reservaSeleccionada.asStateFlow()

    private val _entrenamientosReserva = MutableStateFlow<List<EntrenamientoResponse>>(emptyList())
    val entrenamientosReserva: StateFlow<List<EntrenamientoResponse>> =
        _entrenamientosReserva.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarCatalogos(token: String) {
        viewModelScope.launch {
            try {
                val entrenadoresResponse = API.apiDao.getAdminEntrenadores("Bearer $token")

                if (entrenadoresResponse.isSuccessful) {
                    _entrenadores.value = entrenadoresResponse.body()
                        ?.entrenadores
                        ?.filter { it.activo }
                        ?.map { it.nombre }
                        ?.sortedBy { it.lowercase() }
                        ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar entrenadores: ${entrenadoresResponse.code()} ${entrenadoresResponse.errorBody()?.string()}"
                }

                val ubicacionesResponse = API.apiDao.getAdminUbicaciones("Bearer $token")

                if (ubicacionesResponse.isSuccessful) {
                    _ubicaciones.value = ubicacionesResponse.body()
                        ?.ubicaciones
                        ?.filter { it.activo }
                        ?.map { it.nombre }
                        ?.sortedBy { it.lowercase() }
                        ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar ubicaciones: ${ubicacionesResponse.code()} ${ubicacionesResponse.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    fun cargarReservas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getAdminReservas("Bearer $token")

                if (response.isSuccessful) {
                    _reservas.value = response.body()?.reservas ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar reservas admin: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarReservaPorId(
        token: String,
        idReserva: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getAdminReservaById(
                    token = "Bearer $token",
                    idReserva = idReserva
                )

                if (response.isSuccessful) {
                    _reservaSeleccionada.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar reserva admin: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarPagado(
        token: String,
        idReserva: Int,
        pagado: Boolean
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.updateReservaPagado(
                    token = "Bearer $token",
                    idReserva = idReserva,
                    request = ReservaPagadoUpdateRequest(pagado = pagado)
                )

                if (response.isSuccessful) {
                    _reservaSeleccionada.value = response.body()?.reserva
                    cargarReservas(token)
                } else {
                    _errorMessage.value =
                        "Error al actualizar pago: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun asignarEntrenamientos(
        token: String,
        idReserva: Int,
        entrenamientos: List<EntrenamientoCreateRequest>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = ReservaEntrenamientosAsignarRequest(
                    entrenamientos = entrenamientos.map { entrenamiento ->
                        EntrenamientoAsignacionRequest(
                            nombreEntrenador = entrenamiento.nombreEntrenador,
                            ubicacion = entrenamiento.ubicacion,
                            horaInicio = entrenamiento.horaInicio,
                            horaFin = entrenamiento.horaFin
                        )
                    }
                )

                val response = API.apiDao.asignarEntrenamientosReserva(
                    token = "Bearer $token",
                    idReserva = idReserva,
                    request = request
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value =
                        "Error al asignar entrenamientos: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarEntrenamientosReserva(
        token: String,
        idReserva: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _entrenamientosReserva.value = emptyList()

            try {
                val response = API.apiDao.getEntrenamientosReservaAdmin(
                    token = "Bearer $token",
                    idReserva = idReserva
                )

                if (response.isSuccessful) {
                    _entrenamientosReserva.value = response.body()?.entrenamientos ?: emptyList()
                    onSuccess()
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

    fun editarEntrenamientos(
        token: String,
        idReserva: Int,
        entrenamientos: List<EntrenamientoCreateRequest>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = ReservaEntrenamientosAsignarRequest(
                    entrenamientos = entrenamientos.map { entrenamiento ->
                        EntrenamientoAsignacionRequest(
                            nombreEntrenador = entrenamiento.nombreEntrenador,
                            ubicacion = entrenamiento.ubicacion,
                            horaInicio = entrenamiento.horaInicio,
                            horaFin = entrenamiento.horaFin
                        )
                    }
                )

                val response = API.apiDao.updateEntrenamientosReservaAdmin(
                    token = "Bearer $token",
                    idReserva = idReserva,
                    request = request
                )

                if (response.isSuccessful) {
                    _entrenamientosReserva.value = response.body()?.entrenamientos ?: emptyList()
                    onSuccess()
                } else {
                    _errorMessage.value =
                        "Error al editar entrenamientos: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarReservaSeleccionada() {
        _reservaSeleccionada.value = null
    }

    fun limpiarEntrenamientosReserva() {
        _entrenamientosReserva.value = emptyList()
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}

