package com.example.labball_tfg.ViewModel

import com.google.firebase.auth.FirebaseAuth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiViewModel : ViewModel() {



    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println("Login correcto: ${user?.email}")
                } else {
                    println("Error login: ${task.exception?.message}")
                }
            }
    }
    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    println("Usuario creado: ${user?.email}")
                } else {
                    println("Error registro: ${task.exception?.message}")
                }
            }
    }
}