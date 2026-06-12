package com.example.labball_tfg.ViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.labball_tfg.Vistas.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labball_tfg.Vistas.Admin.AdminScaffoldScreen
import com.example.labball_tfg.Vistas.Cliente.ClienteScaffoldScreen
import com.example.labball_tfg.Vistas.OlvidoPasswordScreen
import com.example.labball_tfg.Vistas.RegisterScreen

// Mantiene el estado y las operaciones de main activity para la interfaz.
class MainActivity : ComponentActivity() {
    // Encapsula la operacion on create usada por la pantalla o el estado.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

// Encapsula la operacion app navigation usada por la pantalla o el estado.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var tokenUsuario by remember { mutableStateOf("") }

    NavHost(navController, "login") {
        composable("login") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("register")
                },
                onLoginSuccess = { token, usuario ->
                    tokenUsuario = token
                    val destino = if (usuario.esAdmin || usuario.esSuperAdmin) "admin" else "cliente"

                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate("olvido_password")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("olvido_password") {
            OlvidoPasswordScreen(
                onEmailSent = {
                    navController.popBackStack()
                }
            )
        }

        composable("cliente") {
            ClienteScaffoldScreen(
                token = tokenUsuario,
                onChangePasswordClick = {
                    navController.navigate("olvido_password")
                },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    tokenUsuario = ""

                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("admin") {
            AdminScaffoldScreen(
                token = tokenUsuario,
                onChangePasswordClick = {
                    navController.navigate("olvido_password")
                },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    tokenUsuario = ""

                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }


    }
}
