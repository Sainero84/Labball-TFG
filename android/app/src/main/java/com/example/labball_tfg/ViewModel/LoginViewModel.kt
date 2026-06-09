package com.example.labball_tfg.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.ViewModel.Firebase.FirebaseAuthManager
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.UsuarioResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Manager que contiene la lógica de Firebase Authentication
    private val firebaseAuthManager = FirebaseAuthManager()

    /**
     * Inicia sesión con Firebase.
     *
     * Si el login es correcto,
     * devuelve el ID Token del usuario.
     */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String, UsuarioResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        // Validación básica
        if (email.isBlank()) {
            onError("Introduce un correo electrónico")
            return
        }

        if (password.isBlank()) {
            onError("Introduce una contraseña")
            return
        }

        // Login contra Firebase
        firebaseAuthManager.loginWithEmailAndPassword(
            email = email,
            password = password,
            onSuccess = { token ->
                viewModelScope.launch {
                    try {
                        val response = API.apiDao.login("Bearer $token")
                        val usuario = response.body()?.usuario

                        if (response.isSuccessful && usuario != null) {
                            onSuccess(token, usuario)
                        } else {
                            onError(
                                "Error al validar usuario: " +
                                        "${response.code()} ${response.errorBody()?.string()}"
                            )
                        }
                    } catch (e: Exception) {
                        onError("Error conectando con FastAPI: ${e.message}")
                    }
                }
            },
            onError = onError
        )
    }
}