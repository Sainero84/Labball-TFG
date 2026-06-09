package com.example.labball_tfg.Vistas

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.labball_tfg.ViewModel.UsuarioViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.errorColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    token: String,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: UsuarioViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var showPhotoOptions by remember { mutableStateOf(false) }
    var showBirthDatePicker by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf<PerfilEditField?>(null) }
    var editInput by remember { mutableStateOf("") }

    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var localBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val openEditDialog: (PerfilEditField) -> Unit = { field ->
        editInput = when (field) {
            PerfilEditField.Nombre -> usuario?.nombre.orEmpty()
            PerfilEditField.Apellido -> usuario?.apellido1.orEmpty()
            PerfilEditField.Telefono -> usuario?.telefono.orEmpty()
        }
        editingField = field
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localImageUri = uri
            localBitmap = null

            val contentType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = getFileNameFromUri(context, uri) ?: "perfil.jpg"
            val bytes = context.contentResolver.openInputStream(uri)
                ?.use { it.readBytes() }

            if (bytes != null) {
                viewModel.subirFotoPerfil(
                    token = token,
                    bytes = bytes,
                    contentType = contentType,
                    fileName = fileName
                )
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            localBitmap = bitmap
            localImageUri = null

            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)

            viewModel.subirFotoPerfil(
                token = token,
                bytes = output.toByteArray(),
                contentType = "image/jpeg",
                fileName = "perfil.jpg"
            )
        }
    }

    LaunchedEffect(token) {
        viewModel.cargarUsuarioMe(token)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = usuario.toRoleLabel(),
                color = textColor.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.weight(0.10f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(textColor.copy(alpha = 0.05f))
                .clickable { showPhotoOptions = true }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            when {
                localBitmap != null -> {
                    Image(
                        bitmap = localBitmap!!.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                localImageUri != null -> {
                    AsyncImage(
                        model = localImageUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                !usuario?.fotoPerfilUrl.isNullOrBlank() -> {
                    AsyncImage(
                        model = usuario?.fotoPerfilUrl,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Foto de perfil",
                        tint = textColor,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        PerfilInfoBox(
            title = "Nombre:",
            value = usuario?.nombre.takeIf { !it.isNullOrBlank() } ?: "Anadir nombre",
            onClick = { openEditDialog(PerfilEditField.Nombre) }
        )

        Spacer(modifier = Modifier.height(7.dp))

        PerfilInfoBox(
            title = "Primer apellido:",
            value = usuario?.apellido1.takeIf { !it.isNullOrBlank() } ?: "Anadir apellido",
            onClick = { openEditDialog(PerfilEditField.Apellido) }
        )

        Spacer(modifier = Modifier.height(7.dp))

        PerfilInfoBox(
            title = "Fecha de nacimiento:",
            value = usuario?.fechaNacimiento.toPerfilDateLabel().ifBlank { "Anadir fecha" },
            onClick = { showBirthDatePicker = true }
        )

        Spacer(modifier = Modifier.height(7.dp))

        PerfilInfoBox(
            title = "Correo electronico:",
            value = usuario?.correo ?: ""
        )

        Spacer(modifier = Modifier.height(7.dp))

        PerfilInfoBox(
            title = "Telefono:",
            value = usuario?.telefono ?: "Anadir telefono",
            onClick = { openEditDialog(PerfilEditField.Telefono) }
        )

        Spacer(modifier = Modifier.weight(0.16f))

        Text(
            text = "Cambiar contrasena",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onChangePasswordClick() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = errorColor
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(42.dp)
        ) {
            Text(
                text = "LOG OUT",
                color = textColor
            )
        }

        Spacer(modifier = Modifier.weight(0.24f))

        if (isLoading) {
            Spacer(modifier = Modifier.height(18.dp))
            CircularProgressIndicator(
                color = primaryColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage ?: "",
                color = textColor
            )
        }
    }

    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false },
            containerColor = backgroundColor
        ) {
            TextButton(
                onClick = {
                    showPhotoOptions = false
                    cameraLauncher.launch(null)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir camara", color = textColor)
            }

            TextButton(
                onClick = {
                    showPhotoOptions = false
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = primaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Elegir de galeria", color = textColor)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showBirthDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showBirthDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = perfilFormatDateMillis(millis)
                            if (hasAtLeast16Years(selectedDate)) {
                                viewModel.actualizarPerfil(
                                    token = token,
                                    nombre = usuario?.nombre.orEmpty(),
                                    apellido1 = usuario?.apellido1.orEmpty(),
                                    fechaNacimiento = selectedDate,
                                    telefono = usuario?.telefono
                                )
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "Debes tener al menos 16 anos",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showBirthDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = primaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDatePicker = false }) {
                    Text("Cancelar", color = textColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    editingField?.let { field ->
        val fechaNormalizada = normalizarFechaPerfil(usuario?.fechaNacimiento.toPerfilDateInput())
        val canSave = when (field) {
            PerfilEditField.Nombre,
            PerfilEditField.Apellido -> editInput.isNotBlank()
            PerfilEditField.Telefono -> editInput.isNotBlank()
        }

        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { editingField = null },
            title = {
                Text(field.dialogTitle, color = textColor)
            },
            text = {
                PerfilDialogTextField(
                    value = editInput,
                    onValueChange = { editInput = it },
                    label = field.fieldLabel,
                    placeholder = field.placeholder,
                    keyboardType = field.keyboardType,
                    isError = false
                )
            },
            confirmButton = {
                TextButton(
                    enabled = canSave && !isLoading,
                    onClick = {
                        if (field == PerfilEditField.Telefono) {
                            viewModel.actualizarTelefono(
                                token = token,
                                telefono = editInput.ifBlank { null }
                            )
                        } else {
                            viewModel.actualizarPerfil(
                                token = token,
                                nombre = if (field == PerfilEditField.Nombre) {
                                    editInput.trim()
                                } else {
                                    usuario?.nombre.orEmpty()
                                },
                                apellido1 = if (field == PerfilEditField.Apellido) {
                                    editInput.trim()
                                } else {
                                    usuario?.apellido1.orEmpty()
                                },
                                fechaNacimiento = fechaNormalizada.orEmpty(),
                                telefono = usuario?.telefono
                            )
                        }
                        editingField = null
                    }
                ) {
                    Text("Guardar", color = primaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingField = null }) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }
}

private fun getFileNameFromUri(
    context: Context,
    uri: Uri
): String? {
    return context.contentResolver.query(uri, null, null, null, null)
        ?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        }
}

@Composable
private fun PerfilInfoBox(
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, primaryColor),
                shape = RoundedCornerShape(50)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = textColor.copy(alpha = 0.65f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PerfilDialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    placeholder: String = "",
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder)
            }
        },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = primaryColor,
            errorBorderColor = errorColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = primaryColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor.copy(alpha = 0.65f),
            focusedPlaceholderColor = textColor.copy(alpha = 0.45f),
            unfocusedPlaceholderColor = textColor.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun com.example.labball_tfg.Modelo.UsuarioMeResponse?.toRoleLabel(): String {
    return when {
        this?.esSuperAdmin == true -> "Super-admin"
        this?.esAdmin == true -> "Admin"
        else -> "Cliente"
    }
}

private enum class PerfilEditField(
    val dialogTitle: String,
    val fieldLabel: String,
    val placeholder: String = "",
    val keyboardType: KeyboardType
) {
    Nombre(
        dialogTitle = "Nombre",
        fieldLabel = "Nombre",
        keyboardType = KeyboardType.Text
    ),
    Apellido(
        dialogTitle = "Primer apellido",
        fieldLabel = "Primer apellido",
        keyboardType = KeyboardType.Text
    ),
    Telefono(
        dialogTitle = "Telefono",
        fieldLabel = "Telefono",
        keyboardType = KeyboardType.Phone
    )
}

private fun String?.toPerfilDateInput(): String {
    val value = this.orEmpty()
    return if (value.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
        val parts = value.split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        value
    }
}

private fun String?.toPerfilDateLabel(): String {
    return toPerfilDateInput()
}

private fun normalizarFechaPerfil(fecha: String): String? {
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

private fun perfilFormatDateMillis(millis: Long): String {
    val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    return String.format(
        Locale.ROOT,
        "%04d-%02d-%02d",
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

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

@Composable
@Preview(showBackground = true)
fun PerfilScreenPreview() {
    PerfilScreen("", {}, {})
}
