package com.example.labball_tfg.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.UsuarioFotoPerfilUpdateRequest
import com.example.labball_tfg.Modelo.UsuarioMeResponse
import com.example.labball_tfg.Modelo.UsuarioPerfilUpdateRequest
import com.example.labball_tfg.Modelo.UsuarioTelefonoUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class UsuarioViewModel : ViewModel() {

    private val _usuario = MutableStateFlow<UsuarioMeResponse?>(null)
    val usuario: StateFlow<UsuarioMeResponse?> = _usuario.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarUsuarioMe(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getUsuarioMe("Bearer $token")

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al cargar usuario: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarTelefono(
        token: String,
        telefono: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.updateTelefonoMe(
                    token = "Bearer $token",
                    request = UsuarioTelefonoUpdateRequest(telefono = telefono)
                )

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al actualizar telefono: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarPerfil(
        token: String,
        nombre: String,
        apellido1: String,
        fechaNacimiento: String,
        telefono: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.updatePerfilMe(
                    token = "Bearer $token",
                    request = UsuarioPerfilUpdateRequest(
                        nombre = nombre,
                        apellido1 = apellido1,
                        fechaNacimiento = fechaNacimiento,
                        telefono = telefono
                    )
                )

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al actualizar perfil: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarFotoPerfil(
        token: String,
        fotoPerfilUrl: String,
        fotoPerfilMimeType: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.updateFotoPerfilMe(
                    token = "Bearer $token",
                    request = UsuarioFotoPerfilUpdateRequest(
                        fotoPerfilUrl = fotoPerfilUrl,
                        fotoPerfilMimeType = fotoPerfilMimeType
                    )
                )

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al actualizar foto de perfil: ${response.code()} ${response.errorBody()?.string()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subirFotoPerfil(
        token: String,
        bytes: ByteArray,
        contentType: String,
        fileName: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val body = bytes.toRequestBody(contentType.toMediaTypeOrNull())

                val response = API.apiDao.uploadFotoPerfil(
                    token = "Bearer $token",
                    contentType = contentType,
                    fileName = fileName,
                    body = body
                )

                if (response.isSuccessful) {
                    _usuario.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al subir foto: ${response.code()} ${response.errorBody()?.string()}"
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
