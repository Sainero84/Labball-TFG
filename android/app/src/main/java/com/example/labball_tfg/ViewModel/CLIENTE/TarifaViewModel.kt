package com.example.labball_tfg.ViewModel.CLIENTE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.TarifaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TarifaViewModel : ViewModel() {

    private val _tarifas = MutableStateFlow<List<TarifaResponse>>(emptyList())
    val tarifas: StateFlow<List<TarifaResponse>> = _tarifas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarTarifas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getTarifas("Bearer $token")

                if (response.isSuccessful) {
                    _tarifas.value = response.body()?.tarifas ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar tarifas: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }
}