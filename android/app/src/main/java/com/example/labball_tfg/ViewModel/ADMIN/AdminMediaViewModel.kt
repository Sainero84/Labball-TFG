package com.example.labball_tfg.ViewModel.ADMIN

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.MediaCreateRequest
import com.example.labball_tfg.Modelo.MediaResponse
import com.example.labball_tfg.Modelo.MediaUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AdminMediaViewModel : ViewModel() {

    private val _mediaGuardado = MutableStateFlow<MediaResponse?>(null)
    val mediaGuardado: StateFlow<MediaResponse?> = _mediaGuardado.asStateFlow()

    private val _mediaEliminado = MutableStateFlow(false)
    val mediaEliminado: StateFlow<Boolean> = _mediaEliminado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun crearVideo(
        token: String,
        media: MediaCreateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _mediaGuardado.value = null
            _mediaEliminado.value = false

            try {
                val response = API.apiDao.createVideo(
                    token = "Bearer $token",
                    request = media
                )

                if (response.isSuccessful) {
                    _mediaGuardado.value = response.body()?.media
                } else {
                    _errorMessage.value =
                        "Error al crear video: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarVideo(
        token: String,
        idMedia: Int,
        media: MediaUpdateRequest
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _mediaGuardado.value = null
            _mediaEliminado.value = false

            try {
                val response = API.apiDao.updateVideo(
                    token = "Bearer $token",
                    idMedia = idMedia,
                    request = media
                )

                if (response.isSuccessful) {
                    _mediaGuardado.value = response.body()?.media
                } else {
                    _errorMessage.value =
                        "Error al actualizar video: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarVideo(
        token: String,
        idMedia: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _mediaGuardado.value = null
            _mediaEliminado.value = false

            try {
                val response = API.apiDao.deleteVideo(
                    token = "Bearer $token",
                    idMedia = idMedia
                )

                if (response.isSuccessful) {
                    _mediaEliminado.value = true
                } else {
                    _errorMessage.value =
                        "Error al eliminar video: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarEstado() {
        _mediaGuardado.value = null
        _mediaEliminado.value = false
        _errorMessage.value = null
    }

    fun subirVideo(
        token: String,
        titulo: String,
        descripcion: String,
        archivo: File,
        mimeType: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _mediaGuardado.value = null
            _mediaEliminado.value = false

            try {
                val tituloBody =
                    titulo.toRequestBody("text/plain".toMediaTypeOrNull())

                val descripcionBody =
                    descripcion.toRequestBody("text/plain".toMediaTypeOrNull())

                val videoBody =
                    archivo.asRequestBody(mimeType.toMediaTypeOrNull())

                val videoPart = MultipartBody.Part.createFormData(
                    "video_file",
                    archivo.name,
                    videoBody
                )

                val response = API.apiDao.uploadVideo(
                    token = "Bearer $token",
                    titulo = tituloBody,
                    descripcion = descripcionBody,
                    videoFile = videoPart
                )

                if (response.isSuccessful) {
                    _mediaGuardado.value = response.body()?.media
                } else {
                    _errorMessage.value =
                        "Error al subir video: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subirVideoDesdeUri(
        token: String,
        titulo: String,
        descripcion: String,
        videoUri: Uri,
        mimeType: String,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _mediaGuardado.value = null
            _mediaEliminado.value = false

            var archivoTemporal: File? = null

            try {
                archivoTemporal = withContext(Dispatchers.IO) {
                    val extension = when (mimeType) {
                        "video/mp4" -> ".mp4"
                        "video/webm" -> ".webm"
                        "video/quicktime" -> ".mov"
                        else -> ".video"
                    }

                    File.createTempFile("labball_video_", extension, context.cacheDir).also { file ->
                        try {
                            context.contentResolver.openInputStream(videoUri)?.use { input ->
                                file.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            } ?: error("No se pudo leer el video seleccionado")
                        } catch (e: Exception) {
                            file.delete()
                            throw e
                        }
                    }
                }

                val videoFile = archivoTemporal ?: error("No se pudo preparar el video seleccionado")

                val tituloBody =
                    titulo.toRequestBody("text/plain".toMediaTypeOrNull())

                val descripcionBody =
                    descripcion.toRequestBody("text/plain".toMediaTypeOrNull())

                val videoBody =
                    videoFile.asRequestBody(mimeType.toMediaTypeOrNull())

                val videoPart = MultipartBody.Part.createFormData(
                    "video_file",
                    videoFile.name,
                    videoBody
                )

                val response = API.apiDao.uploadVideo(
                    token = "Bearer $token",
                    titulo = tituloBody,
                    descripcion = descripcionBody,
                    videoFile = videoPart
                )

                if (response.isSuccessful) {
                    _mediaGuardado.value = response.body()?.media
                } else {
                    _errorMessage.value =
                        "Error al subir video: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al subir video: ${e.message}"
            } finally {
                withContext(Dispatchers.IO) {
                    archivoTemporal?.delete()
                }
                _isLoading.value = false
            }
        }
    }
}
