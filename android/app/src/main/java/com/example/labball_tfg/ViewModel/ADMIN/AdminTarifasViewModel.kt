package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.TarifaCreateRequest
import com.example.labball_tfg.Modelo.TarifaResponse
import com.example.labball_tfg.Modelo.TarifaUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminTarifasViewModel : ViewModel() {

    private val _tarifas = MutableStateFlow<List<TarifaResponse>>(emptyList())
    val tarifas: StateFlow<List<TarifaResponse>> = _tarifas.asStateFlow()

    private val _tarifaSeleccionada = MutableStateFlow<TarifaResponse?>(null)
    val tarifaSeleccionada: StateFlow<TarifaResponse?> = _tarifaSeleccionada.asStateFlow()

    private val _tarifaGuardada = MutableStateFlow<TarifaResponse?>(null)
    val tarifaGuardada: StateFlow<TarifaResponse?> = _tarifaGuardada.asStateFlow()

    private val _tarifaEliminada = MutableStateFlow(false)
    val tarifaEliminada: StateFlow<Boolean> = _tarifaEliminada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarTarifas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getAdminTarifas("Bearer $token")

                if (response.isSuccessful) {
                    _tarifas.value = response.body()?.tarifas ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar tarifas admin: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarTarifaPorId(
        token: String,
        idTarifa: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getAdminTarifaById(
                    token = "Bearer $token",
                    idTarifa = idTarifa
                )

                if (response.isSuccessful) {
                    _tarifaSeleccionada.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar tarifa: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearTarifa(
        token: String,
        tarifa: TarifaCreateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _tarifaGuardada.value = null
            _tarifaEliminada.value = false

            try {
                val response = API.apiDao.createTarifa(
                    token = "Bearer $token",
                    request = tarifa
                )

                if (response.isSuccessful) {
                    _tarifaGuardada.value = response.body()?.tarifa
                    cargarTarifas(token)
                } else {
                    _errorMessage.value =
                        "Error al crear tarifa: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarTarifa(
        token: String,
        idTarifa: Int,
        tarifa: TarifaUpdateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _tarifaGuardada.value = null
            _tarifaEliminada.value = false

            try {
                val response = API.apiDao.updateTarifa(
                    token = "Bearer $token",
                    idTarifa = idTarifa,
                    request = tarifa
                )

                if (response.isSuccessful) {
                    _tarifaGuardada.value = response.body()?.tarifa
                    cargarTarifas(token)
                } else {
                    _errorMessage.value =
                        "Error al actualizar tarifa: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarTarifa(
        token: String,
        idTarifa: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _tarifaGuardada.value = null
            _tarifaEliminada.value = false

            try {
                val response = API.apiDao.deleteTarifa(
                    token = "Bearer $token",
                    idTarifa = idTarifa
                )

                if (response.isSuccessful) {
                    _tarifaEliminada.value = true
                    cargarTarifas(token)
                } else {
                    _errorMessage.value =
                        "Error al eliminar tarifa: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarTarifaSeleccionada() {
        _tarifaSeleccionada.value = null
    }

    fun limpiarEstado() {
        _tarifaGuardada.value = null
        _tarifaEliminada.value = false
        _errorMessage.value = null
    }
}
