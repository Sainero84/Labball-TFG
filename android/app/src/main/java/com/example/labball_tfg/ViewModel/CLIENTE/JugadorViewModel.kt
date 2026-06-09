package com.example.labball_tfg.ViewModel.CLIENTE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.JugadorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JugadorViewModel : ViewModel() {

    private val _jugador = MutableStateFlow<JugadorResponse?>(null)
    val jugador: StateFlow<JugadorResponse?> = _jugador.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarJugadorMe(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getJugadorMe("Bearer $token")

                if (response.isSuccessful) {
                    _jugador.value = response.body()
                } else if (response.code() == 404) {
                    _jugador.value = null
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

    fun limpiarJugador() {
        _jugador.value = null
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}
