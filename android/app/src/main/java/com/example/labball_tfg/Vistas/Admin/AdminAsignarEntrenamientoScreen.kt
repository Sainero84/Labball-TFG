package com.example.labball_tfg.Vistas.Admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labball_tfg.Modelo.EntrenamientoCreateRequest
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import com.example.labball_tfg.Modelo.EntrenadorResponse
import com.example.labball_tfg.Modelo.ReservaResponse
import com.example.labball_tfg.Modelo.UbicacionResponse
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val FIELD_COUNT = 7
private const val FIELD_ENTRENADOR_ID = 0
private const val FIELD_ENTRENADOR_NOMBRE = 1
private const val FIELD_UBICACION_ID = 2
private const val FIELD_UBICACION_NOMBRE = 3
private const val FIELD_FECHA_VISIBLE = 4
private const val FIELD_FECHA_API = 5
private const val FIELD_HORA_INICIO = 6

private data class AdminCatalogOption(
    val id: Int,
    val nombre: String
)

private data class AdminDateTarget(
    val sessionIndex: Int
)

private data class AdminTimeTarget(
    val sessionIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAsignarEntrenamientoScreen(
    reserva: ReservaResponse,
    idJugador: Int?,
    entrenamientosIniciales: List<EntrenamientoResponse> = emptyList(),
    entrenadores: List<EntrenadorResponse> = emptyList(),
    ubicaciones: List<UbicacionResponse> = emptyList(),
    editMode: Boolean = false,
    onBack: () -> Unit,
    onGuardarEntrenamientos: (List<EntrenamientoCreateRequest>) -> Unit
) {
    val totalSesiones = reserva.numeroSesiones

    var values by rememberSaveable(reserva.idReserva, totalSesiones, editMode, entrenamientosIniciales.size) {
        mutableStateOf(
            adminInitialTrainingValues(
                totalSesiones = totalSesiones,
                entrenamientos = entrenamientosIniciales
            )
        )
    }

    var dateTarget by remember { mutableStateOf<AdminDateTarget?>(null) }
    var timeTarget by remember { mutableStateOf<AdminTimeTarget?>(null) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val completedSessions = remember(values, totalSesiones) {
        (0 until totalSesiones).count { index ->
            values.adminTrainingSessionComplete(index)
        }
    }

    val allCompleted = completedSessions == totalSesiones && idJugador != null

    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AdminAsignarHeader(
                reserva = reserva,
                completedSessions = completedSessions,
                totalSesiones = totalSesiones,
                editMode = editMode
            )
        }

        items(
            items = (0 until totalSesiones).toList(),
            key = { index -> "entrenamiento_$index" }
        ) { index ->
            AdminEntrenamientoFormCard(
                index = index,
                values = values,
                entrenadores = entrenadores,
                ubicaciones = ubicaciones,
                onValueChange = { field, value ->
                    values = values.adminUpdateTrainingField(index, field, value)
                },
                onDateClick = {
                    dateTarget = AdminDateTarget(index)
                },
                onTimeClick = {
                    timeTarget = AdminTimeTarget(index)
                }
            )
        }

        item {
            if (idJugador == null) {
                Text(
                    text = "No se puede guardar porque esta reserva no tiene jugador asociado.",
                    color = Color(0xFFFF8A80),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { showConfirmDialog = true },
                enabled = allCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = secondaryColor,
                    disabledContainerColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (editMode) "Guardar cambios" else "Guardar asignacion",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    dateTarget?.let { target ->
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { dateTarget = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            values = values
                                .adminUpdateTrainingField(
                                    target.sessionIndex,
                                    FIELD_FECHA_VISIBLE,
                                    adminFormatDate(millis, "dd/MM/yyyy")
                                )
                                .adminUpdateTrainingField(
                                    target.sessionIndex,
                                    FIELD_FECHA_API,
                                    adminFormatDate(millis, "yyyy-MM-dd")
                                )
                        }
                        dateTarget = null
                    }
                ) {
                    Text("Aceptar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { dateTarget = null }) {
                    Text("Cancelar", color = textColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    timeTarget?.let { target ->
        val timePickerState = rememberTimePickerState(is24Hour = true)

        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { timeTarget = null },
            title = {
                Text(
                    text = "Seleccionar hora",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        values = values.adminUpdateTrainingField(
                            target.sessionIndex,
                            FIELD_HORA_INICIO,
                            "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                        )
                        timeTarget = null
                    }
                ) {
                    Text("Aceptar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { timeTarget = null }) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Confirmar asignacion",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (editMode) {
                        "Se guardaran los cambios de $totalSesiones entrenamientos de ${reserva.nombre} ${reserva.apellidos}."
                    } else {
                        "Se asignaran $totalSesiones entrenamientos a ${reserva.nombre} ${reserva.apellidos}."
                    },
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val jugadorId = idJugador ?: return@TextButton
                        showConfirmDialog = false
                        onGuardarEntrenamientos(
                            values.adminToEntrenamientoRequests(
                                totalSesiones = totalSesiones,
                                idJugador = jugadorId,
                                idUsuario = reserva.idUsuario
                            )
                        )
                    }
                ) {
                    Text("Guardar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }
}

@Composable
private fun AdminAsignarHeader(
    reserva: ReservaResponse,
    completedSessions: Int,
    totalSesiones: Int,
    editMode: Boolean
) {
    val progress = if (totalSesiones == 0) 0f else completedSessions.toFloat() / totalSesiones

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, secondaryColor, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${reserva.nombre} ${reserva.apellidos}",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = reserva.correo,
            color = Color.Gray,
            fontSize = 13.sp
        )

        Text(
            text = if (editMode) {
                "$completedSessions/$totalSesiones entrenamientos listos para editar"
            } else {
                "$completedSessions/$totalSesiones entrenamientos completos"
            },
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.DarkGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(secondaryColor)
            )
        }
    }
}

@Composable
private fun AdminEntrenamientoFormCard(
    index: Int,
    values: List<String>,
    entrenadores: List<EntrenadorResponse>,
    ubicaciones: List<UbicacionResponse>,
    onValueChange: (Int, String) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    val completed = values.adminTrainingSessionComplete(index)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, secondaryColor, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Sesion ${index + 1}",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        AdminTrainingSelector(
            value = values.adminTrainingField(index, FIELD_ENTRENADOR_NOMBRE),
            placeholder = "Entrenador",
            options = adminEntrenadorOptions(
                catalog = entrenadores,
                currentId = values.adminTrainingField(index, FIELD_ENTRENADOR_ID),
                currentName = values.adminTrainingField(index, FIELD_ENTRENADOR_NOMBRE)
            ),
            onSelected = {
                onValueChange(FIELD_ENTRENADOR_ID, it.id.toString())
                onValueChange(FIELD_ENTRENADOR_NOMBRE, it.nombre)
            }
        )

        AdminTrainingSelector(
            value = values.adminTrainingField(index, FIELD_UBICACION_NOMBRE),
            placeholder = "Ubicacion",
            options = adminUbicacionOptions(
                catalog = ubicaciones,
                currentId = values.adminTrainingField(index, FIELD_UBICACION_ID),
                currentName = values.adminTrainingField(index, FIELD_UBICACION_NOMBRE)
            ),
            onSelected = {
                onValueChange(FIELD_UBICACION_ID, it.id.toString())
                onValueChange(FIELD_UBICACION_NOMBRE, it.nombre)
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminPickerBox(
                text = values.adminTrainingField(index, FIELD_FECHA_VISIBLE),
                placeholder = "Fecha",
                modifier = Modifier.weight(1f),
                onClick = onDateClick
            )

            AdminPickerBox(
                text = values.adminTrainingField(index, FIELD_HORA_INICIO),
                placeholder = "Hora inicio",
                modifier = Modifier.weight(1f),
                onClick = onTimeClick
            )
        }

        Text(
            text = adminTrainingEndText(
                dateVisible = values.adminTrainingField(index, FIELD_FECHA_VISIBLE),
                dateApi = values.adminTrainingField(index, FIELD_FECHA_API),
                startTime = values.adminTrainingField(index, FIELD_HORA_INICIO)
            ),
            color = if (completed) textColor else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminTrainingTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = Color.Gray)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = secondaryColor,
            focusedBorderColor = secondaryColor,
            unfocusedBorderColor = secondaryColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AdminPickerBox(
    text: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, secondaryColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text.ifBlank { placeholder },
            color = if (text.isBlank()) Color.Gray else textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun List<String>.adminTrainingField(index: Int, field: Int): String {
    return this[index * FIELD_COUNT + field]
}

private fun List<String>.adminUpdateTrainingField(
    index: Int,
    field: Int,
    value: String
): List<String> {
    val updated = toMutableList()
    updated[index * FIELD_COUNT + field] = value
    return updated
}

private fun List<String>.adminTrainingSessionComplete(index: Int): Boolean {
    return adminTrainingField(index, FIELD_ENTRENADOR_ID).isNotBlank() &&
            adminTrainingField(index, FIELD_UBICACION_ID).isNotBlank() &&
            adminTrainingField(index, FIELD_FECHA_API).isNotBlank() &&
            adminTrainingField(index, FIELD_HORA_INICIO).isNotBlank()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminTrainingSelector(
    value: String,
    placeholder: String,
    options: List<AdminCatalogOption>,
    onSelected: (AdminCatalogOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(text = placeholder, color = Color.Gray)
            },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textColor
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = secondaryColor,
                focusedBorderColor = secondaryColor,
                unfocusedBorderColor = secondaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = backgroundColor
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No hay opciones",
                            color = Color.Gray
                        )
                    },
                    onClick = { expanded = false }
                )
            }

            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.nombre,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun adminEntrenadorOptions(
    catalog: List<EntrenadorResponse>,
    currentId: String,
    currentName: String
): List<AdminCatalogOption> {
    val options = catalog.map { entrenador ->
        AdminCatalogOption(
            id = entrenador.idEntrenador,
            nombre = entrenador.nombre
        )
    }

    return adminCatalogOptions(options, currentId, currentName)
}

private fun adminUbicacionOptions(
    catalog: List<UbicacionResponse>,
    currentId: String,
    currentName: String
): List<AdminCatalogOption> {
    val options = catalog.map { ubicacion ->
        AdminCatalogOption(
            id = ubicacion.idUbicacion,
            nombre = ubicacion.nombre
        )
    }

    return adminCatalogOptions(options, currentId, currentName)
}

private fun adminCatalogOptions(
    catalog: List<AdminCatalogOption>,
    currentId: String,
    currentName: String
): List<AdminCatalogOption> {
    val currentOption = currentId.toIntOrNull()
        ?.takeIf { currentName.isNotBlank() }
        ?.let { id -> AdminCatalogOption(id = id, nombre = currentName) }

    return (catalog + listOfNotNull(currentOption))
        .distinctBy { it.id }
        .sortedBy { it.nombre.lowercase() }
}
private fun adminInitialTrainingValues(
    totalSesiones: Int,
    entrenamientos: List<EntrenamientoResponse>
): List<String> {
    val values = MutableList(totalSesiones * FIELD_COUNT) { "" }

    entrenamientos.take(totalSesiones).forEachIndexed { index, entrenamiento ->
        values[index * FIELD_COUNT + FIELD_ENTRENADOR_ID] = entrenamiento.idEntrenador.toString()
        values[index * FIELD_COUNT + FIELD_ENTRENADOR_NOMBRE] = entrenamiento.nombreEntrenador
        values[index * FIELD_COUNT + FIELD_UBICACION_ID] = entrenamiento.idUbicacion.toString()
        values[index * FIELD_COUNT + FIELD_UBICACION_NOMBRE] = entrenamiento.ubicacion
        values[index * FIELD_COUNT + FIELD_FECHA_VISIBLE] =
            adminVisibleDateFromDateTime(entrenamiento.horaInicio)
        values[index * FIELD_COUNT + FIELD_FECHA_API] =
            adminApiDateFromDateTime(entrenamiento.horaInicio)
        values[index * FIELD_COUNT + FIELD_HORA_INICIO] =
            adminTimeFromDateTime(entrenamiento.horaInicio)
    }

    return values
}

private fun List<String>.adminToEntrenamientoRequests(
    totalSesiones: Int,
    idJugador: Int,
    idUsuario: Int
): List<EntrenamientoCreateRequest> {
    return List(totalSesiones) { index ->
        EntrenamientoCreateRequest(
            idEntrenador = adminTrainingField(index, FIELD_ENTRENADOR_ID).toInt(),
            idUbicacion = adminTrainingField(index, FIELD_UBICACION_ID).toInt(),
            nombreEntrenador = adminTrainingField(index, FIELD_ENTRENADOR_NOMBRE).trim(),
            ubicacion = adminTrainingField(index, FIELD_UBICACION_NOMBRE).trim(),
            horaInicio = adminTrainingDateTimeValue(
                adminTrainingField(index, FIELD_FECHA_API),
                adminTrainingField(index, FIELD_HORA_INICIO)
            ),
            horaFin = adminTrainingEndDateTimeValue(
                adminTrainingField(index, FIELD_FECHA_API),
                adminTrainingField(index, FIELD_HORA_INICIO)
            ),
            idJugador = idJugador,
            idUsuario = idUsuario
        )
    }
}

private fun adminTrainingDateTimeValue(
    date: String,
    time: String
): String {
    if (date.isBlank() || time.isBlank()) return ""
    return "${date}T${time}:00"
}

private fun adminTrainingEndDateTimeValue(
    date: String,
    time: String
): String {
    if (date.isBlank() || time.isBlank()) return ""

    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()

    calendar.time = parser.parse("$date $time") ?: return ""
    calendar.add(Calendar.HOUR_OF_DAY, 1)

    return formatter.format(calendar.time)
}

private fun adminTrainingEndText(
    dateVisible: String,
    dateApi: String,
    startTime: String
): String {
    if (dateVisible.isBlank() || dateApi.isBlank() || startTime.isBlank()) {
        return "Fin automatico: 1 hora despues"
    }

    val end = adminTrainingEndDateTimeValue(dateApi, startTime)
    if (end.isBlank()) return "Fin automatico: 1 hora despues"

    return "Fin: $dateVisible ${end.substring(11, 16)}"
}

private fun adminApiDateFromDateTime(value: String): String {
    return if (value.length >= 10) value.substring(0, 10) else ""
}

private fun adminVisibleDateFromDateTime(value: String): String {
    val date = adminApiDateFromDateTime(value)
    val parts = date.split("-")
    return if (parts.size == 3) {
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        ""
    }
}

private fun adminTimeFromDateTime(value: String): String {
    val timeStart = value.indexOf("T").takeIf { it >= 0 } ?: value.indexOf(" ")
    if (timeStart < 0 || value.length < timeStart + 6) return ""
    return value.substring(timeStart + 1, timeStart + 6)
}

private fun adminFormatDate(
    millis: Long,
    pattern: String
): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
}


