package com.example.labball_tfg.Vistas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.R
import com.example.labball_tfg.ViewModel.RegisterViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Renderiza la pantalla register screen y conecta sus acciones principales.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var apellido1 by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp),
        Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Labball",
            modifier = Modifier.size(126.dp)
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Registro",
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(18.dp))

        RegisterTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Nombre",
            keyboardType = KeyboardType.Text,
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = apellido1,
            onValueChange = { apellido1 = it },
            placeholder = "Apellido/s",
            keyboardType = KeyboardType.Text,
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        RegisterDateField(
            value = fechaNacimiento,
            onClick = { if (!loading) showDatePicker = true },
            placeholder = "Fecha nacimiento",
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Correo electronico",
            keyboardType = KeyboardType.Email,
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Contraseña",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Comprobar contraseña",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            enabled = !loading
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(
                        context,
                        "Las contrasenas no coinciden",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                loading = true

                viewModel.registerUser(
                    email = email,
                    password = password,
                    nombre = nombre,
                    apellido1 = apellido1,
                    fechaNacimiento = fechaNacimiento,
                    onSuccess = {
                        loading = false
                        Toast.makeText(
                            context,
                            "Usuario creado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        onRegisterSuccess()
                    },
                    onError = { error ->
                        loading = false
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            Modifier.fillMaxWidth(0.72f),
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
                Text("Crear cuenta")
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Ya tengo cuenta",
            color = textColor,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(enabled = !loading) {
                onLoginClick()
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = formatDateMillis(millis, "yyyy-MM-dd")
                            if (hasAtLeast16Years(selectedDate)) {
                                fechaNacimiento = selectedDate
                            } else {
                                Toast.makeText(
                                    context,
                                    "Debes tener al menos 16 anos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = textColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Encapsula la operacion register text field usada por la pantalla o el estado.
@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    enabled: Boolean,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = primaryColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = primaryColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedPlaceholderColor = textColor,
            unfocusedPlaceholderColor = textColor,
            disabledBorderColor = primaryColor,
            disabledTextColor = textColor,
            disabledPlaceholderColor = textColor,
            disabledContainerColor = Color.Transparent
        ),
        modifier = Modifier.widthIn(min = 260.dp, max = 320.dp)
    )
}

// Encapsula la operacion register date field usada por la pantalla o el estado.
@Composable
private fun RegisterDateField(
    value: String,
    onClick: () -> Unit,
    placeholder: String,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .widthIn(min = 260.dp, max = 320.dp)
            .clickable(enabled = enabled) { onClick() }
    ) {
        OutlinedTextField(
            value = value.toDisplayDate(),
            onValueChange = {},
            placeholder = { Text(placeholder) },
            singleLine = true,
            enabled = false,
            readOnly = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = primaryColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = primaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = textColor,
                unfocusedPlaceholderColor = textColor,
                disabledBorderColor = primaryColor,
                disabledTextColor = textColor,
                disabledPlaceholderColor = textColor,
                disabledContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .widthIn(min = 260.dp, max = 320.dp)
        )
    }
}

// Encapsula la operacion format date millis usada por la pantalla o el estado.
private fun formatDateMillis(
    millis: Long,
    pattern: String
): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    return String.format(
        Locale.ROOT,
        pattern
            .replace("yyyy", "%04d")
            .replace("MM", "%02d")
            .replace("dd", "%02d"),
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

// Encapsula la operacion string usada por la pantalla o el estado.
private fun String.toDisplayDate(): String {
    return if (matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
        val parts = split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        this
    }
}

// Encapsula la operacion has at least16 years usada por la pantalla o el estado.
private fun hasAtLeast16Years(date: String): Boolean {
    val parts = date.split("-")
    if (parts.size != 3) {
        return false
    }

    val birthDate = Calendar.getInstance().apply {
        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val limit = Calendar.getInstance().apply {
        add(Calendar.YEAR, -16)
    }

    return !birthDate.after(limit)
}
