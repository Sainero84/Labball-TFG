package com.example.labball_tfg.ViewModel.Firebase

import com.google.firebase.auth.FirebaseAuth


// Mantiene el estado y las operaciones de firebase auth manager para la interfaz.
class FirebaseAuthManager {

    // Instancia de Firebase Authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Envia el correo de recuperacion de contrasena mediante Firebase Authentication.
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Firebase se encarga de enviar el email automáticamente
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                // Se ejecuta si el email se ha enviado correctamente
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Se ejecuta si ocurre algún error (email no registrado, formato incorrecto, etc.)
                onError(exception.message ?: "Error al enviar el correo")
            }
    }

    // Crea el usuario en Firebase y devuelve su UID junto al token de sesion.
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (firebaseUid: String, token: String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                val firebaseUid = user?.uid

                if (user == null || firebaseUid == null) {
                    onError("No se pudo obtener el UID de Firebase")
                    return@addOnSuccessListener
                }

                user.getIdToken(false)
                    .addOnSuccessListener { tokenResult ->
                        val token = tokenResult.token
                        if (token != null) {
                            onSuccess(firebaseUid, token)
                        } else {
                            onError("No se pudo obtener el token")
                        }
                    }
                    .addOnFailureListener {
                        onError(it.message ?: "Error al obtener el token")
                    }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error al crear el usuario")
            }
    }

    // Inicia sesion en Firebase y obtiene el token para validarlo en FastAPI.
    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val user = result.user

                if (user == null) {
                    onError("No se pudo obtener el usuario de Firebase")
                    return@addOnSuccessListener
                }

                user.getIdToken(true)
                    .addOnSuccessListener { tokenResult ->
                        val token = tokenResult.token

                        if (token != null) {
                            onSuccess(token)
                        } else {
                            onError("No se pudo obtener el token")
                        }
                    }
                    .addOnFailureListener { exception ->
                        onError(exception.message ?: "Error al obtener el token")
                    }
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Error al iniciar sesión")
            }
    }

}
