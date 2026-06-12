package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.AdminUsuarioResponse
import com.example.labball_tfg.Modelo.JugadorCreateRequest
import com.example.labball_tfg.Modelo.JugadorEstadisticasUpdateRequest
import com.example.labball_tfg.Modelo.JugadorResponse
import com.example.labball_tfg.Modelo.JugadorUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Mantiene el estado y las operaciones de admin jugadores view model para la interfaz.
class AdminJugadoresViewModel : ViewModel() {

    private val _jugadores = MutableStateFlow<List<JugadorResponse>>(emptyList())
    val jugadores: StateFlow<List<JugadorResponse>> = _jugadores.asStateFlow()

    private val _usuariosClientes = MutableStateFlow<List<AdminUsuarioResponse>>(emptyList())
    val usuariosClientes: StateFlow<List<AdminUsuarioResponse>> = _usuariosClientes.asStateFlow()

    private val _entrenadores = MutableStateFlow<List<String>>(emptyList())
    val entrenadores: StateFlow<List<String>> = _entrenadores.asStateFlow()

    private val _ubicaciones = MutableStateFlow<List<String>>(emptyList())
    val ubicaciones: StateFlow<List<String>> = _ubicaciones.asStateFlow()

    private val _jugadorSeleccionado = MutableStateFlow<JugadorResponse?>(null)
    val jugadorSeleccionado: StateFlow<JugadorResponse?> = _jugadorSeleccionado.asStateFlow()

    private val _jugadorGuardado = MutableStateFlow<JugadorResponse?>(null)
    val jugadorGuardado: StateFlow<JugadorResponse?> = _jugadorGuardado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Carga cargar catalogos desde la API y actualiza el estado asociado.
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

    // Carga cargar usuarios clientes desde la API y actualiza el estado asociado.
    fun cargarUsuariosClientes(token: String) {
        viewModelScope.launch {
            try {
                val response = API.apiDao.getAdminUsuarios("Bearer $token")

                if (response.isSuccessful) {
                    _usuariosClientes.value = response.body()
                        ?.usuarios
                        ?.filter { !it.esAdmin && !it.esSuperAdmin }
                        ?.sortedBy { it.correo.lowercase() }
                        ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar usuarios cliente: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    // Carga cargar jugadores desde la API y actualiza el estado asociado.
    fun cargarJugadores(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.getAdminJugadores("Bearer $token")

                if (response.isSuccessful) {
                    _jugadores.value = response.body()?.jugadores ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar jugadores: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga cargar jugador por id desde la API y actualiza el estado asociado.
    fun cargarJugadorPorId(
        token: String,
        idJugador: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.getAdminJugadorById(
                    token = "Bearer $token",
                    idJugador = idJugador
                )

                if (response.isSuccessful) {
                    _jugadorSeleccionado.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar jugador: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crea crear jugador mediante la API y comunica el resultado.
    fun crearJugador(
        token: String,
        jugador: JugadorCreateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _jugadorGuardado.value = null

            try {
                val response = API.apiDao.createJugador(
                    token = "Bearer $token",
                    request = jugador
                )

                if (response.isSuccessful) {
                    _jugadorGuardado.value = response.body()?.jugador
                    _successMessage.value = response.body()?.message ?: "Jugador creado correctamente"
                    cargarJugadores(token)
                } else {
                    _errorMessage.value =
                        "Error al crear jugador: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualiza actualizar jugador y refleja la respuesta en la pantalla.
    fun actualizarJugador(
        token: String,
        idJugador: Int,
        jugador: JugadorUpdateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _jugadorGuardado.value = null

            try {
                val response = API.apiDao.updateJugador(
                    token = "Bearer $token",
                    idJugador = idJugador,
                    request = jugador
                )

                if (response.isSuccessful) {
                    val jugadorActualizado = response.body()?.jugador
                    _jugadorGuardado.value = jugadorActualizado
                    _jugadorSeleccionado.value = jugadorActualizado
                    _successMessage.value = response.body()?.message ?: "Jugador actualizado correctamente"
                    cargarJugadores(token)
                } else {
                    _errorMessage.value =
                        "Error al actualizar jugador: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualiza actualizar estadisticas y refleja la respuesta en la pantalla.
    fun actualizarEstadisticas(
        token: String,
        idJugador: Int,
        estadisticas: JugadorEstadisticasUpdateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _jugadorGuardado.value = null

            try {
                val response = API.apiDao.updateJugadorEstadisticas(
                    token = "Bearer $token",
                    idJugador = idJugador,
                    request = estadisticas
                )

                if (response.isSuccessful) {
                    val jugadorActualizado = response.body()?.jugador
                    _jugadorGuardado.value = jugadorActualizado
                    _jugadorSeleccionado.value = jugadorActualizado
                    _successMessage.value = response.body()?.message ?: "Estadisticas actualizadas correctamente"
                    cargarJugadores(token)
                } else {
                    _errorMessage.value =
                        "Error al actualizar estadisticas: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpia limpiar jugador seleccionado para reiniciar el estado temporal.
    fun limpiarJugadorSeleccionado() {
        _jugadorSeleccionado.value = null
    }

    // Limpia limpiar estado para reiniciar el estado temporal.
    fun limpiarEstado() {
        _jugadorGuardado.value = null
        _errorMessage.value = null
        _successMessage.value = null
    }
}

