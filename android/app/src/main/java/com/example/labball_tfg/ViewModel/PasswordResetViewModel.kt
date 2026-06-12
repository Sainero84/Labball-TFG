package com.example.labball_tfg.ViewModel

import androidx.lifecycle.ViewModel
import com.example.labball_tfg.ViewModel.Firebase.FirebaseAuthManager

// Mantiene el estado y las operaciones de password reset view model para la interfaz.
class PasswordResetViewModel : ViewModel() {

    // Instancia del manager que se comunica con Firebase
    private val firebaseAuthManager = FirebaseAuthManager()

    // Solicita a Firebase el envio del correo de recuperacion de contrasena.
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