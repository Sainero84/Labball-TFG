package com.example.labball_tfg.ViewModel.ADMIN

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.AdminUsuarioCreateRequest
import com.example.labball_tfg.Modelo.AdminUsuarioResponse
import com.example.labball_tfg.Modelo.AdminUsuarioRolUpdateRequest
import com.example.labball_tfg.Modelo.DescuentoAdminCreateRequest
import com.example.labball_tfg.Modelo.DescuentoResponse
import com.example.labball_tfg.Modelo.DescuentoUpdateRequest
import com.example.labball_tfg.Modelo.UbicacionCreateRequest
import com.example.labball_tfg.Modelo.UbicacionResponse
import com.example.labball_tfg.Modelo.UsuarioMeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Mantiene el estado y las operaciones de admin usuarios view model para la interfaz.
class AdminUsuariosViewModel : ViewModel() {

    private val _usuarios = MutableStateFlow<List<AdminUsuarioResponse>>(emptyList())
    val usuarios: StateFlow<List<AdminUsuarioResponse>> = _usuarios.asStateFlow()

    private val _descuentos = MutableStateFlow<List<DescuentoResponse>>(emptyList())
    val descuentos: StateFlow<List<DescuentoResponse>> = _descuentos.asStateFlow()

    private val _ubicaciones = MutableStateFlow<List<UbicacionResponse>>(emptyList())
    val ubicaciones: StateFlow<List<UbicacionResponse>> = _ubicaciones.asStateFlow()

    private val _usuarioActual = MutableStateFlow<UsuarioMeResponse?>(null)
    val usuarioActual: StateFlow<UsuarioMeResponse?> = _usuarioActual.asStateFlow()

    private val _usuarioGuardado = MutableStateFlow<AdminUsuarioResponse?>(null)
    val usuarioGuardado: StateFlow<AdminUsuarioResponse?> = _usuarioGuardado.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Carga cargar negocio desde la API y actualiza el estado asociado.
    fun cargarNegocio(token: String) {
        cargarUsuarioActual(token)
        cargarUsuarios(token)
        cargarDescuentos(token)
        cargarUbicaciones(token)
    }

    // Carga cargar usuario actual desde la API y actualiza el estado asociado.
    fun cargarUsuarioActual(token: String) {
        viewModelScope.launch {
            try {
                val response = API.apiDao.getUsuarioMe("Bearer $token")

                if (response.isSuccessful) {
                    _usuarioActual.value = response.body()
                } else {
                    _errorMessage.value =
                        "Error al validar permisos: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    // Carga cargar usuarios desde la API y actualiza el estado asociado.
    fun cargarUsuarios(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = API.apiDao.getAdminUsuarios("Bearer $token")

                if (response.isSuccessful) {
                    _usuarios.value = response.body()?.usuarios ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar usuarios: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Carga cargar descuentos desde la API y actualiza el estado asociado.
    fun cargarDescuentos(token: String) {
        viewModelScope.launch {
            try {
                val response = API.apiDao.getAdminDescuentos("Bearer $token")

                if (response.isSuccessful) {
                    _descuentos.value = response.body()?.descuentos ?: emptyList()
                } else if (response.code() != 403) {
                    _errorMessage.value =
                        "Error al cargar descuentos: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    // Carga cargar ubicaciones desde la API y actualiza el estado asociado.
    fun cargarUbicaciones(token: String) {
        viewModelScope.launch {
            try {
                val response = API.apiDao.getAdminUbicaciones("Bearer $token")

                if (response.isSuccessful) {
                    _ubicaciones.value = response.body()?.ubicaciones ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error al cargar ubicaciones: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            }
        }
    }

    // Crea crear usuario mediante la API y comunica el resultado.
    fun crearUsuario(
        token: String,
        correo: String,
        esAdmin: Boolean,
        esSuperAdmin: Boolean,
        esEntrenador: Boolean
    ) {
        if (!puedeAsignarRol(esAdmin, esSuperAdmin)) {
            _errorMessage.value = "No tienes permisos para asignar este rol."
            return
        }

        if (esEntrenador && !esAdmin && !esSuperAdmin) {
            _errorMessage.value = "Un entrenador debe ser administrador."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _usuarioGuardado.value = null

            try {
                val response = API.apiDao.createAdminUsuario(
                    token = "Bearer $token",
                    request = AdminUsuarioCreateRequest(
                        correo = correo.trim(),
                        esAdmin = esAdmin || esSuperAdmin,
                        esSuperAdmin = esSuperAdmin,
                        esEntrenador = esEntrenador
                    )
                )

                if (response.isSuccessful) {
                    _usuarioGuardado.value = response.body()?.usuario
                    _successMessage.value = response.body()?.message ?: "Usuario creado correctamente"
                    cargarUsuarios(token)
                } else {
                    _errorMessage.value =
                        "Error al crear usuario: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualiza actualizar rol y refleja la respuesta en la pantalla.
    fun actualizarRol(
        token: String,
        idUsuario: Int,
        esAdmin: Boolean,
        esSuperAdmin: Boolean,
        esEntrenador: Boolean
    ) {
        val usuarioObjetivo = _usuarios.value.firstOrNull { it.idUsuario == idUsuario }
        val usuarioActual = _usuarioActual.value

        if (usuarioActual?.idUsuario == idUsuario) {
            _errorMessage.value = "No puedes cambiar tu propio rol desde esta pantalla."
            return
        }

        if (!puedeAsignarRol(esAdmin, esSuperAdmin)) {
            _errorMessage.value = "No tienes permisos para asignar este rol."
            return
        }

        if (esEntrenador && !esAdmin && !esSuperAdmin) {
            _errorMessage.value = "Un entrenador debe ser administrador."
            return
        }

        if (esEntrenador && usuarioActual?.esSuperAdmin != true) {
            _errorMessage.value = "Solo un super-admin puede asignar entrenadores."
            return
        }

        if ((usuarioObjetivo?.esAdmin == true || usuarioObjetivo?.esSuperAdmin == true) &&
            usuarioActual?.esSuperAdmin != true
        ) {
            _errorMessage.value = "Solo un super-admin puede modificar usuarios administradores."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            _usuarioGuardado.value = null

            try {
                val response = API.apiDao.updateAdminUsuarioRol(
                    token = "Bearer $token",
                    idUsuario = idUsuario,
                    request = AdminUsuarioRolUpdateRequest(
                        esAdmin = esAdmin || esSuperAdmin,
                        esSuperAdmin = esSuperAdmin,
                        esEntrenador = esEntrenador
                    )
                )

                if (response.isSuccessful) {
                    _usuarioGuardado.value = response.body()?.usuario
                    _successMessage.value = response.body()?.message ?: "Usuario actualizado correctamente"
                    cargarUsuarios(token)
                    cargarDescuentos(token)
                } else {
                    _errorMessage.value =
                        "Error al cambiar rol: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Elimina eliminar usuario y sincroniza el estado local.
    fun eliminarUsuario(token: String, idUsuario: Int) {
        val usuarioActual = _usuarioActual.value

        if (usuarioActual?.idUsuario == idUsuario) {
            _errorMessage.value = "No puedes eliminar tu propia cuenta desde esta pantalla."
            return
        }

        if (usuarioActual?.esSuperAdmin != true) {
            _errorMessage.value = "Solo un super-admin puede eliminar usuarios."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.deleteAdminUsuario(
                    token = "Bearer $token",
                    idUsuario = idUsuario
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Usuario eliminado correctamente"
                    cargarUsuarios(token)
                } else {
                    _errorMessage.value =
                        "Error al eliminar usuario: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crea crear descuento mediante la API y comunica el resultado.
    fun crearDescuento(token: String, codigo: String, porcentajeTexto: String) {
        val porcentaje = porcentajeTexto.trim().replace(",", ".").toDoubleOrNull()

        if (porcentaje == null || porcentaje <= 0.0 || porcentaje > 100.0) {
            _errorMessage.value = "Introduce un porcentaje entre 1 y 100."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.createAdminDescuento(
                    token = "Bearer $token",
                    request = DescuentoAdminCreateRequest(
                        codigo = codigo.trim().uppercase().takeIf { it.isNotBlank() },
                        porcentaje = porcentaje
                    )
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Descuento creado correctamente"
                    cargarDescuentos(token)
                } else {
                    _errorMessage.value =
                        "Error al crear descuento: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualiza actualizar descuento y refleja la respuesta en la pantalla.
    fun actualizarDescuento(
        token: String,
        idDescuento: Int,
        codigo: String,
        porcentajeTexto: String
    ) {
        val porcentaje = porcentajeTexto.trim().replace(",", ".").toDoubleOrNull()

        if (codigo.isBlank()) {
            _errorMessage.value = "Introduce el codigo del descuento."
            return
        }

        if (porcentaje == null || porcentaje <= 0.0 || porcentaje > 100.0) {
            _errorMessage.value = "Introduce un porcentaje entre 1 y 100."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.updateAdminDescuento(
                    token = "Bearer $token",
                    idDescuento = idDescuento,
                    request = DescuentoUpdateRequest(
                        codigo = codigo.trim().uppercase(),
                        porcentaje = porcentaje
                    )
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Descuento actualizado correctamente"
                    cargarDescuentos(token)
                } else {
                    _errorMessage.value =
                        "Error al actualizar descuento: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Elimina eliminar descuento y sincroniza el estado local.
    fun eliminarDescuento(token: String, idDescuento: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.deleteAdminDescuento(
                    token = "Bearer $token",
                    idDescuento = idDescuento
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Descuento eliminado correctamente"
                    cargarDescuentos(token)
                } else {
                    _errorMessage.value =
                        "Error al eliminar descuento: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Crea crear ubicacion mediante la API y comunica el resultado.
    fun crearUbicacion(token: String, nombre: String) {
        val nombreLimpio = nombre.trim()

        if (nombreLimpio.isBlank()) {
            _errorMessage.value = "Introduce el nombre de la ubicacion."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.createAdminUbicacion(
                    token = "Bearer $token",
                    request = UbicacionCreateRequest(nombre = nombreLimpio)
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Ubicacion creada correctamente"
                    cargarUbicaciones(token)
                } else {
                    _errorMessage.value =
                        "Error al crear ubicacion: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Elimina eliminar ubicacion y sincroniza el estado local.
    fun eliminarUbicacion(token: String, idUbicacion: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                val response = API.apiDao.deleteAdminUbicacion(
                    token = "Bearer $token",
                    idUbicacion = idUbicacion
                )

                if (response.isSuccessful) {
                    _successMessage.value = response.body()?.message ?: "Ubicacion eliminada correctamente"
                    cargarUbicaciones(token)
                } else {
                    _errorMessage.value =
                        "Error al eliminar ubicacion: ${response.code()} ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error conectando con FastAPI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpia limpiar estado para reiniciar el estado temporal.
    fun limpiarEstado() {
        _usuarioGuardado.value = null
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Encapsula la operacion puede asignar rol usada por la pantalla o el estado.
    private fun puedeAsignarRol(esAdmin: Boolean, esSuperAdmin: Boolean): Boolean {
        val usuarioActual = _usuarioActual.value

        return when {
            esSuperAdmin -> usuarioActual?.esSuperAdmin == true
            esAdmin -> usuarioActual?.esAdmin == true || usuarioActual?.esSuperAdmin == true
            else -> usuarioActual?.esAdmin == true || usuarioActual?.esSuperAdmin == true
        }
    }
}
