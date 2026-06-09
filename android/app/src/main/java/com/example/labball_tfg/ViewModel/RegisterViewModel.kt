package com.example.labball_tfg.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labball_tfg.Modelo.API
import com.example.labball_tfg.Modelo.UsuarioCreateRequest
import com.example.labball_tfg.ViewModel.Firebase.FirebaseAuthManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class RegisterViewModel : ViewModel() {

    private val firebaseAuthManager = FirebaseAuthManager()

    fun registerUser(
        email: String,
        password: String,
        nombre: String,
        apellido1: String,
        fechaNacimiento: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank()) {
            onError("Introduce un correo electronico")
            return
        }

        if (password.isBlank()) {
            onError("Introduce una contrasena")
            return
        }

        if (password.length < 6) {
            onError("La contrasena debe tener al menos 6 caracteres")
            return
        }

        if (nombre.isBlank()) {
            onError("Introduce tu nombre")
            return
        }

        if (apellido1.isBlank()) {
            onError("Introduce tu primer apellido")
            return
        }

        val fechaNormalizada = normalizarFechaNacimiento(fechaNacimiento)
        if (fechaNormalizada == null) {
            onError("Introduce la fecha de nacimiento en formato dd/mm/aaaa")
            return
        }

        if (!tieneAlMenos16(fechaNormalizada)) {
            onError("Debes tener al menos 16 anos")
            return
        }

        firebaseAuthManager.createUserWithEmailAndPassword(
            email,
            password,
            { firebaseUid, token ->
                val nuevoUsuario = UsuarioCreateRequest(
                    firebase_uid = firebaseUid,
                    correo = email,
                    nombre = nombre.trim(),
                    apellido1 = apellido1.trim(),
                    fechaNacimiento = fechaNormalizada
                )

                viewModelScope.launch {
                    try {
                        val response = API.apiDao.register(
                            "Bearer $token",
                            nuevoUsuario
                        )

                        if (response.isSuccessful) {
                            onSuccess()
                        } else {
                            onError("Error al guardar el usuario en MySQL")
                        }
                    } catch (e: Exception) {
                        onError("Error conectando con FastAPI: ${e.message}")
                    }
                }
            },
            onError = onError
        )
    }

    private fun normalizarFechaNacimiento(fecha: String): String? {
        val limpia = fecha.trim()

        val partes = when {
            limpia.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> {
                val split = limpia.split("-")
                listOf(split[2], split[1], split[0])
            }
            limpia.matches(Regex("^\\d{1,2}/\\d{1,2}/\\d{4}$")) -> limpia.split("/")
            limpia.matches(Regex("^\\d{1,2}-\\d{1,2}-\\d{4}$")) -> limpia.split("-")
            else -> return null
        }

        val dia = partes.getOrNull(0)?.toIntOrNull() ?: return null
        val mes = partes.getOrNull(1)?.toIntOrNull() ?: return null
        val anio = partes.getOrNull(2)?.toIntOrNull() ?: return null
        val anioActual = Calendar.getInstance().get(Calendar.YEAR)

        if (dia !in 1..31 || mes !in 1..12 || anio !in 1900..anioActual) {
            return null
        }

        return String.format(Locale.ROOT, "%04d-%02d-%02d", anio, mes, dia)
    }

    private fun tieneAlMenos16(fechaNormalizada: String): Boolean {
        val partes = fechaNormalizada.split("-")
        val nacimiento = Calendar.getInstance().apply {
            isLenient = false
            set(
                partes[0].toInt(),
                partes[1].toInt() - 1,
                partes[2].toInt(),
                0,
                0,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }

        val limite = Calendar.getInstance().apply {
            add(Calendar.YEAR, -16)
        }

        return !nacimiento.after(limite)
    }
}
