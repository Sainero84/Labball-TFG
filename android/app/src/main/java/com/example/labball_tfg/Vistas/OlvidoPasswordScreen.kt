package com.example.labball_tfg.Vistas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.R
import com.example.labball_tfg.ViewModel.PasswordResetViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.secondaryTextColor
import com.example.labball_tfg.ui.theme.textColor

@Composable
fun OlvidoPasswordScreen(
    viewModel: PasswordResetViewModel = viewModel(),
    onEmailSent: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Labball",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Olvido de Contraseña",
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier.widthIn(min = 260.dp, max = 320.dp),
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico") },
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
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Le enviaremos un correo para que cambie su contraseña. Recuerda poner un correo válido.",
                color = secondaryTextColor,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    loading = true

                    viewModel.sendPasswordResetEmail(
                        email = email,
                        onSuccess = {
                            loading = false
                            Toast.makeText(
                                context,
                                "Correo enviado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            onEmailSent()
                        },
                        onError = { error ->
                            loading = false
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
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
                    Text("Enviar Código")
                }
            }
        }
    }
}
