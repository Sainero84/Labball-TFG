package com.example.labball_tfg.ViewModel.CLIENTE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.VideoDetailResponse
import com.example.labball_tfg.Modelo.VideoListItemResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Mantiene el estado y las operaciones de media view model para la interfaz.
class MediaViewModel : ViewModel() {

    private val _videos = MutableStateFlow<List<VideoListItemResponse>>(emptyList())
    val videos: StateFlow<List<VideoListItemResponse>> = _videos.asStateFlow()

    private val _videoSeleccionado = MutableStateFlow<VideoDetailResponse?>(null)
    val videoSeleccionado: StateFlow<VideoDetailResponse?> =
        _videoSeleccionado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Carga cargar videos desde la API y actualiza el estado asociado.
    fun cargarVideos(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getVideos("Bearer $token")

                if (response.isSuccessful) {
                    _videos.value = response.body()?.videos ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar videos: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga cargar video por id desde la API y actualiza el estado asociado.
    fun cargarVideoPorId(
        token: String,
        idMedia: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getVideoById(
                    token = "Bearer $token",
                    idMedia = idMedia
                )

                if (response.isSuccessful) {
                    _videoSeleccionado.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar video: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpia limpiar video seleccionado para reiniciar el estado temporal.
    fun limpiarVideoSeleccionado() {
        _videoSeleccionado.value = null
    }

    // Limpia limpiar error para reiniciar el estado temporal.
    fun limpiarError() {
        _errorMessage.value = null
    }
}