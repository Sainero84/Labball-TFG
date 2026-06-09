package com.example.labball_tfg.Vistas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.R
import com.example.labball_tfg.Modelo.UsuarioResponse
import com.example.labball_tfg.ViewModel.LoginViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String, UsuarioResponse) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor),
        Arrangement.Center,
        Alignment.CenterHorizontally

    ) {
        Image(
            painterResource(id = R.drawable.logo),
            "Labball",
            Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Login",
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            email,
            { email = it },
            placeholder = { Text("Usuario/Correo electrónico") },
            singleLine = true,
            enabled = !loading,
            shape = RoundedCornerShape(50),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = primaryColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = primaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = textColor,
                unfocusedPlaceholderColor = textColor
            ),
            modifier = Modifier.widthIn(min = 260.dp, max = 320.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Contraseña") },
            singleLine = true,
            enabled = !loading,
            shape = RoundedCornerShape(50),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = primaryColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = primaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = textColor,
                unfocusedPlaceholderColor = textColor
            ),
            modifier = Modifier.widthIn(min = 260.dp, max = 320.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))


        Row(
            Modifier
                .fillMaxWidth(0.6f)
                .background(backgroundColor),
            Arrangement.Start,
            Alignment.CenterVertically
        ) {
            Text(
                "¿Has olvidado tu contraseña?",
                Modifier.clickable(enabled = !loading) {
                    onForgotPasswordClick()
                },
                color = textColor,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            {
                loading = true

                viewModel.loginUser(
                    email = email,
                    password = password,
                    onSuccess = { token, usuario ->
                        loading = false
                        Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(token, usuario)
                    },
                    onError = { error ->
                        loading = false
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            Modifier.fillMaxWidth(0.6f),
            enabled = !loading,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
                contentColor = textColor
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = textColor
                )
            } else {
                Text("Acceder")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            "No tengo cuenta",
            color = textColor,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(enabled = !loading) {
                onRegisterClick()
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen()
}

