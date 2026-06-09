package com.example.labball_tfg.ViewModel.Firebase

import com.google.firebase.auth.FirebaseAuth


class FirebaseAuthManager {

    // Instancia de Firebase Authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Envía un email de recuperación de contraseña al usuario
     *
     * @param email correo del usuario
     * @param onSuccess callback si el envío es correcto
     * @param onError callback si ocurre un error
     */
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

    /**
     * Crea un nuevo usuario en Firebase Authentication
     *
     * @param email correo del usuario
     * @param password contraseña del usuario
     * @param onSuccess acción si el registro es correcto
     * @param onError acción si ocurre un error
     */
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


    /**
     * Inicia sesión en Firebase con email y contraseña.
     *
     * Si el login es correcto, obtiene el ID Token.
     * Este token se podrá enviar a FastAPI para validar al usuario.
     */
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

