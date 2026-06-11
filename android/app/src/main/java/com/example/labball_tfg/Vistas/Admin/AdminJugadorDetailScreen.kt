package com.example.labball_tfg.Vistas.Admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.labball_tfg.Modelo.JugadorEstadisticasUpdateRequest
import com.example.labball_tfg.Modelo.JugadorResponse
import com.example.labball_tfg.Modelo.JugadorUpdateRequest
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.errorColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.text.Normalizer
import java.util.Locale

private val adminJugadorPositions = listOf(
    "Base",
    "Escolta",
    "Alero",
    "Ala-Pívot",
    "Pívot"
)

@Composable
fun AdminJugadorDetailScreen(
    jugador: JugadorResponse?,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onActualizarDatos: (JugadorUpdateRequest) -> Unit,
    onActualizarEstadisticas: (JugadorEstadisticasUpdateRequest) -> Unit
) {
    var initializedPlayerId by rememberSaveable { mutableStateOf<Int?>(null) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var posicion by rememberSaveable { mutableStateOf("") }
    var altura by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }

    var tiro by rememberSaveable { mutableStateOf("") }
    var fisico by rememberSaveable { mutableStateOf("") }
    var bote by rememberSaveable { mutableStateOf("") }
    var pase by rememberSaveable { mutableStateOf("") }
    var defensa by rememberSaveable { mutableStateOf("") }
    var velocidad by rememberSaveable { mutableStateOf("") }

    var showPlayerDataEditor by rememberSaveable { mutableStateOf(false) }
    var showNameConfirmation by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(jugador?.idJugador) {
        if (jugador != null && initializedPlayerId != jugador.idJugador) {
            nombre = jugador.nombre
            apellidos = jugador.apellidos
            posicion = jugador.posicion.toSelectablePosition()
            altura = jugador.altura.toDecimalInput()
            peso = jugador.peso.toDecimalInput()
            tiro = (jugador.tiro ?: 0).toString()
            fisico = (jugador.fisico ?: 0).toString()
            bote = (jugador.bote ?: 0).toString()
            pase = (jugador.pase ?: 0).toString()
            defensa = (jugador.defensa ?: 0).toString()
            velocidad = (jugador.velocidad ?: 0).toString()
            initializedPlayerId = jugador.idJugador
        }
    }

    if (jugador == null) {
        AdminJugadorDetailLoading(
            isLoading = isLoading,
            errorMessage = errorMessage,
            onBack = onBack
        )
        return
    }

    val validData = nombre.isNotBlank() &&
        apellidos.isNotBlank() &&
        posicion in adminJugadorPositions &&
        altura.isValidDecimalInput() &&
        peso.isValidDecimalInput()

    val validStats = listOf(
        tiro,
        fisico,
        bote,
        pase,
        defensa,
        velocidad
    ).all { it.isValidStatInput() }

    val saveData = {
        onActualizarDatos(
            JugadorUpdateRequest(
                nombre = nombre.trim(),
                apellidos = apellidos.trim(),
                posicion = posicion,
                altura = altura.toDecimalValue(),
                peso = peso.toDecimalValue()
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = if (showPlayerDataEditor) {
            Arrangement.spacedBy(10.dp)
        } else {
            Arrangement.SpaceEvenly
        }
    ) {
        item {
            AdminJugadorDetailHeader(
                jugador = jugador,
                onBack = onBack,
                onEditPlayerData = {
                    showPlayerDataEditor = !showPlayerDataEditor
                }
            )
        }

        item {
            HorizontalDivider(
                color = textColor,
                thickness = 1.dp
            )
        }

        if (showPlayerDataEditor) {
            item {
                Text(
                    text = "Datos del jugador",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AdminJugadorTextInput(
                    label = "Nombre:",
                    value = nombre,
                    onValueChange = { nombre = it }
                )
            }

            item {
                AdminJugadorTextInput(
                    label = "Apellidos:",
                    value = apellidos,
                    onValueChange = { apellidos = it }
                )
            }

            item {
                AdminJugadorPositionSelector(
                    value = posicion,
                    onValueChange = { posicion = it }
                )
            }

            item {
                AdminJugadorTextInput(
                    label = "Altura:",
                    value = altura,
                    placeholder = "1,90",
                    keyboardType = KeyboardType.Decimal,
                    isError = altura.isNotEmpty() && !altura.isValidDecimalInput(),
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() || char == ',' }) {
                            altura = it
                        }
                    }
                )
            }

            item {
                AdminJugadorTextInput(
                    label = "Peso:",
                    value = peso,
                    placeholder = "80,00",
                    keyboardType = KeyboardType.Decimal,
                    isError = peso.isNotEmpty() && !peso.isValidDecimalInput(),
                    onValueChange = {
                        if (it.length <= 7 && it.all { char -> char.isDigit() || char == ',' }) {
                            peso = it
                        }
                    }
                )
            }

            item {
                AdminJugadorHelpText("La altura y el peso deben llevar una coma (,) y dos decimales.")
            }

            item {
                AdminJugadorCenteredButton(
                    text = "Actualizar datos",
                    enabled = validData && !isLoading,
                    onClick = {
                        val nameChanged = nombre.trim() != jugador.nombre ||
                            apellidos.trim() != jugador.apellidos

                        if (nameChanged) {
                            showNameConfirmation = true
                        } else {
                            saveData()
                        }
                    }
                )
            }

            item {
                HorizontalDivider(
                    color = textColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        item {
            Text(
                text = "Estadísticas",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            AdminJugadorStatInput("Tiro", tiro) { tiro = it }
        }

        item {
            AdminJugadorStatInput("Físico", fisico) { fisico = it }
        }

        item {
            AdminJugadorStatInput("Bote", bote) { bote = it }
        }

        item {
            AdminJugadorStatInput("Pase", pase) { pase = it }
        }

        item {
            AdminJugadorStatInput("Defensa", defensa) { defensa = it }
        }

        item {
            AdminJugadorStatInput("Velocidad", velocidad) { velocidad = it }
        }

        item {
            AdminJugadorHelpText("Las estadísticas deben estar entre 0 y 100.")
        }

        item {
            AdminJugadorCenteredButton(
                text = "Actualizar stats",
                enabled = validStats && !isLoading,
                onClick = {
                    onActualizarEstadisticas(
                        JugadorEstadisticasUpdateRequest(
                            tiro = tiro.toInt(),
                            fisico = fisico.toInt(),
                            bote = bote.toInt(),
                            pase = pase.toInt(),
                            defensa = defensa.toInt(),
                            velocidad = velocidad.toInt()
                        )
                    )
                }
            )
        }

        if (isLoading) {
            item {
                CircularProgressIndicator(
                    color = secondaryColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage,
                    color = errorColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showNameConfirmation) {
        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { showNameConfirmation = false },
            title = {
                Text(
                    text = "Cambiar nombre",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres cambiar el nombre del jugador?",
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNameConfirmation = false
                        saveData()
                    }
                ) {
                    Text("Confirmar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameConfirmation = false }) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }
}

@Composable
private fun AdminJugadorDetailLoading(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = textColor
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                color = secondaryColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = errorColor,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun AdminJugadorDetailHeader(
    jugador: JugadorResponse,
    onBack: () -> Unit,
    onEditPlayerData: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = textColor
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AdminJugadorHeaderAvatar(jugador)

            Text(
                text = "${jugador.nombre} ${jugador.apellidos}",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AdminJugadorHeaderMetadata(jugador.posicion ?: "Sin posición")
                AdminJugadorHeaderMetadata(jugador.altura.toHeightLabel())
                AdminJugadorHeaderMetadata(jugador.peso.toWeightLabel())
            }

            val asignacion = jugador.trainingSummary()
            if (asignacion.isNotBlank()) {
                Text(
                    text = asignacion,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        IconButton(onClick = onEditPlayerData) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar datos del jugador",
                tint = secondaryColor
            )
        }
    }
}

@Composable
private fun AdminJugadorHeaderAvatar(jugador: JugadorResponse) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(textColor.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        if (!jugador.fotoPerfilUrl.isNullOrBlank()) {
            AsyncImage(
                model = jugador.fotoPerfilUrl,
                contentDescription = "Foto de jugador",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = secondaryColor,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Composable
private fun AdminJugadorHeaderMetadata(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 16.sp
    )
}

@Composable
private fun AdminJugadorTextInput(
    label: String,
    value: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.9f)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(placeholder)
                }
            },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = adminJugadorTextFieldColors(),
            shape = RoundedCornerShape(50),
            modifier = Modifier.weight(1.35f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminJugadorPositionSelector(
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Posición:",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.9f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(1.35f)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Seleccionar") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = textColor
                    )
                },
                colors = adminJugadorTextFieldColors(),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = backgroundColor
            ) {
                adminJugadorPositions.forEach { position ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = position,
                                color = textColor
                            )
                        },
                        onClick = {
                            onValueChange(position)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminJugadorStatInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= 3 && it.all(Char::isDigit)) {
                    onValueChange(it)
                }
            },
            singleLine = true,
            isError = value.isNotEmpty() && !value.isValidStatInput(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = adminJugadorTextFieldColors(),
            shape = RoundedCornerShape(50),
            modifier = Modifier.width(126.dp)
        )
    }
}

@Composable
private fun AdminJugadorCenteredButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
                disabledContainerColor = secondaryColor.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = text,
                color = textColor
            )
        }
    }
}

@Composable
private fun AdminJugadorHelpText(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun adminJugadorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = primaryColor,
    errorBorderColor = errorColor,
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = primaryColor,
    focusedContainerColor = backgroundColor,
    unfocusedContainerColor = backgroundColor,
    focusedPlaceholderColor = textColor.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.55f)
)

private fun String.isValidDecimalInput(): Boolean {
    return matches(Regex("^\\d+,\\d{2}$")) && toDecimalValue() != null
}

private fun String.toDecimalValue(): Double? {
    return replace(',', '.').toDoubleOrNull()
}

private fun String.isValidStatInput(): Boolean {
    return toIntOrNull()?.let { it in 0..100 } == true
}

private fun Double?.toDecimalInput(): String {
    return this?.let {
        String.format(Locale("es", "ES"), "%.2f", it)
    }.orEmpty()
}

private fun Double?.toHeightLabel(): String {
    return this?.let {
        "${String.format(Locale("es", "ES"), "%.2f", it)} m"
    } ?: "- m"
}

private fun Double?.toWeightLabel(): String {
    return this?.let {
        "${String.format(Locale("es", "ES"), "%.2f", it)} kg"
    } ?: "- kg"
}

private fun String?.toSelectablePosition(): String {
    val normalizedPosition = this.orEmpty().normalizedSelectorValue()

    return adminJugadorPositions.firstOrNull { position ->
        position.normalizedSelectorValue() == normalizedPosition
    }.orEmpty()
}

private fun String.normalizedSelectorValue(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .replace("-", "")
        .replace(" ", "")
        .lowercase(Locale.ROOT)
}

private fun JugadorResponse.trainingSummary(): String {
    val entrenador = (entrenadores.map { it.nombre } + listOfNotNull(nombreEntrenador))
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() }
    val ubicacionAsignada = (ubicaciones.map { it.nombre } + listOfNotNull(ubicacion))
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() }

    return listOfNotNull(
        entrenador,
        ubicacionAsignada
    ).joinToString(" - ")
}
