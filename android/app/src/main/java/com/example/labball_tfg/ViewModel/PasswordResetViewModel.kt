package com.example.labball_tfg.ViewModel

import androidx.lifecycle.ViewModel
import com.example.labball_tfg.ViewModel.Firebase.FirebaseAuthManager

class PasswordResetViewModel : ViewModel() {

    // Instancia del manager que se comunica con Firebase
    private val firebaseAuthManager = FirebaseAuthManager()

    /**
     * Función que gestiona el envío del email de recuperación de contraseña
     *
     * @param email correo introducido por el usuario
     * @param onSuccess acción a ejecutar si todo va bien
     * @param onError acción a ejecutar si ocurre un error
     */
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        // Validación básica: evitar correos vacíos
        if (email.isBlank()) {
            onError("Introduce un correo electrónico")
            return
        }

        // Llamamos al manager de Firebase para enviar el email
        firebaseAuthManager.sendPasswordResetEmail(
            email,
            onSuccess,
            onError
        )
    }
}