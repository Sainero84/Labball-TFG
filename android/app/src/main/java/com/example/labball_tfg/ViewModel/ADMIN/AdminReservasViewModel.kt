package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.EntrenamientoAsignacionRequest
import com.example.labball_tfg.Modelo.EntrenamientoCreateRequest
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import com.example.labball_tfg.Modelo.EntrenadorResponse
import com.example.labball_tfg.Modelo.ReservaAdminListItemResponse
import com.example.labball_tfg.Modelo.ReservaEntrenamientosAsignarRequest
import com.example.labball_tfg.Modelo.ReservaPagadoUpdateRequest
import com.example.labball_tfg.Modelo.ReservaResponse
import com.example.labball_tfg.Modelo.UbicacionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Mantiene el estado y las operaciones de admin reservas view model para la interfaz.
class AdminReservasViewModel : ViewModel() {

    private val _reservas = MutableStateFlow<List<ReservaAdminListItemResponse>>(emptyList())
    val reservas: StateFlow<List<ReservaAdminListItemResponse>> = _reservas.asStateFlow()

    private val _entrenadores = MutableStateFlow<List<EntrenadorResponse>>(emptyList())
    val entrenadores: StateFlow<List<EntrenadorResponse>> = _entrenadores.asStateFlow()

    private val _ubicaciones = MutableStateFlow<List<UbicacionResponse>>(emptyList())
    val ubicaciones: StateFlow<List<UbicacionResponse>> = _ubicaciones.asStateFlow()

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

    // Carga cargar catalogos desde la API y actualiza el estado asociado.
    fun cargarCatalogos(token: String) {
        viewModelScope.launch {
            try {
                val entrenadoresResponse = API.apiDao.getAdminEntrenadores("Bearer $token")

                if (entrenadoresResponse.isSuccessful) {
                    _entrenadores.value = entrenadoresResponse.body()
                        ?.entrenadores
                        ?.filter { it.activo }
                        ?.sortedBy { it.nombre.lowercase() }
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
                        ?.sortedBy { it.nombre.lowercase() }
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

    // Carga cargar reservas desde la API y actualiza el estado asociado.
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

    // Carga cargar reserva por id desde la API y actualiza el estado asociado.
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

    // Actualiza actualizar pagado y refleja la respuesta en la pantalla.
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

    // Encapsula la operacion asignar entrenamientos usada por la pantalla o el estado.
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
                            idEntrenador = entrenamiento.idEntrenador,
                            idUbicacion = entrenamiento.idUbicacion,
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

    // Carga cargar entrenamientos reserva desde la API y actualiza el estado asociado.
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

    // Encapsula la operacion editar entrenamientos usada por la pantalla o el estado.
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
                            idEntrenador = entrenamiento.idEntrenador,
                            idUbicacion = entrenamiento.idUbicacion,
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

    // Limpia limpiar reserva seleccionada para reiniciar el estado temporal.
    fun limpiarReservaSeleccionada() {
        _reservaSeleccionada.value = null
    }

    // Limpia limpiar entrenamientos reserva para reiniciar el estado temporal.
    fun limpiarEntrenamientosReserva() {
        _entrenamientosReserva.value = emptyList()
    }

    // Limpia limpiar error para reiniciar el estado temporal.
    fun limpiarError() {
        _errorMessage.value = null
    }
}

