package com.example.labball_tfg.Vistas.Cliente.Reservas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.Modelo.ReservaCreateRequest
import com.example.labball_tfg.ViewModel.CLIENTE.ReservaViewModel
import com.example.labball_tfg.ViewModel.CLIENTE.TarifaViewModel
import com.example.labball_tfg.ViewModel.UsuarioViewModel

import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealizarReservaScreen(
    token: String,
    onBackToReservasInicio: () -> Unit,
    reservaViewModel: ReservaViewModel = viewModel(),
    tarifaViewModel: TarifaViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    val tarifas by tarifaViewModel.tarifas.collectAsState()
    val usuario by usuarioViewModel.usuario.collectAsState()
    val usuarioLoading by usuarioViewModel.isLoading.collectAsState()
    val preview by reservaViewModel.reservaPreview.collectAsState()
    val reservaCreada by reservaViewModel.reservaCreada.collectAsState()
    val loading by reservaViewModel.isLoading.collectAsState()
    val error by reservaViewModel.errorMessage.collectAsState()

    var numeroSesiones by remember { mutableIntStateOf(1) }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var club by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var codigoDescuento by remember { mutableStateOf("") }
    var semanasCantidad by remember { mutableIntStateOf(1) }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showBirthDatePicker by remember { mutableStateOf(false) }

    val semanasDisponibles = remember {
        listOf(
            SemanaReserva(1, 2, "23/06 - 27/06"),
            SemanaReserva(3, 4, "30/06 - 04/07"),
            SemanaReserva(5, 6, "07/07 - 11/07"),
            SemanaReserva(7, 8, "14/07 - 18/07")
        )
    }
    val semanasSeleccionadasUi = remember {
        mutableStateListOf(SemanaSeleccionada(semanasDisponibles.first(), HorarioSemana.AM))
    }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(semanasCantidad) {
        while (semanasSeleccionadasUi.size < semanasCantidad) {
            semanasSeleccionadasUi.add(
                siguienteSemanaDisponible(
                    opciones = semanasDisponibles,
                    seleccionadas = semanasSeleccionadasUi
                )
            )
        }

        while (semanasSeleccionadasUi.size > semanasCantidad) {
            semanasSeleccionadasUi.removeAt(semanasSeleccionadasUi.lastIndex)
        }
    }

    val semanasSeleccionadas = semanasSeleccionadasUi.map { it.idSemana }

    LaunchedEffect(token) {
        tarifaViewModel.cargarTarifas(token)
        usuarioViewModel.cargarUsuarioMe(token)
    }

    LaunchedEffect(tarifas) {
        if (tarifas.isNotEmpty()) {
            numeroSesiones = tarifas.minBy { it.numeroSesiones }.numeroSesiones
        }
    }

    LaunchedEffect(numeroSesiones, codigoDescuento) {
        reservaViewModel.previewReserva(
            token = token,
            numeroSesiones = numeroSesiones,
            codigoDescuento = codigoDescuento.ifBlank { null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        ReservaSelectorSesiones(
            value = numeroSesiones,
            opciones = tarifas.map { it.numeroSesiones }.distinct().sorted(),
            onValueChange = { numeroSesiones = it }
        )

        RellenarDatosUsuarioButton(
            isLoading = usuarioLoading,
            onClick = {
                val datosUsuario = usuario

                if (datosUsuario == null) {
                    errorLocal = "No se han podido cargar tus datos de usuario"
                } else {
                    nombre = datosUsuario.nombre.orEmpty()
                    apellidos = datosUsuario.apellido1.orEmpty()
                    fechaNacimiento = datosUsuario.fechaNacimiento.orEmpty()
                    correo = datosUsuario.correo
                    telefono = datosUsuario.telefono.orEmpty()
                    errorLocal = null
                }
            }
        )

        ReservaInput("Nombre:", nombre) { nombre = it }
        ReservaInput("Apellidos:", apellidos) { apellidos = it }
        ReservaInput("DNI/NIE:", dni, placeholder = "Ej: 01234567A") { dni = it }
        ReservaDateInput(
            label = "Fecha de\nNacimiento:",
            value = fechaNacimiento,
            onClick = { showBirthDatePicker = true }
        )
        ReservaInput("Correo\nelectrónico:", correo, keyboardType = KeyboardType.Email) { correo = it }
        ReservaInput("Teléfono:", telefono, keyboardType = KeyboardType.Phone) { telefono = it }
        ReservaInput("Club:", club) { club = it }
        ReservaInput("Categoría:", categoria) { categoria = it }
        ReservaInput("Código\ndescuento:", codigoDescuento) { codigoDescuento = it }

        Spacer(modifier = Modifier.height(10.dp))

        ReservaStepper(
            label = "Semanas\ndisponibles:",
            value = semanasCantidad,
            min = 1,
            max = semanasDisponibles.size * HorarioSemana.values().size,
            onValueChange = { semanasCantidad = it }
        )

        semanasSeleccionadasUi.forEachIndexed { index, seleccion ->
            SemanaSeleccionadaRow(
                label = "Semana\ndisponible ${index + 1}:",
                seleccion = seleccion,
                opciones = semanasDisponibles,
                onSemanaChange = { semana ->
                    val idsOtros = semanasSeleccionadasUi.idsSeleccionadosExcepto(index)
                    val nuevaSeleccion = seleccion
                        .copy(semana = semana)
                        .evitarDuplicado(idsOtros)

                    if (nuevaSeleccion != null) {
                        semanasSeleccionadasUi[index] = nuevaSeleccion
                        errorLocal = null
                    } else {
                        errorLocal = "Esa semana ya tiene AM y PM seleccionados"
                    }
                },
                onHorarioClick = {
                    val idsOtros = semanasSeleccionadasUi.idsSeleccionadosExcepto(index)
                    val nuevaSeleccion = seleccion.copy(horario = seleccion.horario.toggle())

                    if (nuevaSeleccion.idSemana in idsOtros) {
                        errorLocal = "Esa combinacion ya esta seleccionada"
                    } else {
                        semanasSeleccionadasUi[index] = nuevaSeleccion
                        errorLocal = null
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrecioReservaBox(
            precioSinDescuento = preview?.precioSinDescuento,
            descuentoAplicado = preview?.descuentoAplicado,
            precioFinal = preview?.precioFinal
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Realice una transferencia a:\nES80 0000 0000 0000 0000 0000 y envíe el archivo a Labball@gmail.com\nUna vez confirmado el pago, su solicitud pasará a ser PAGADO",
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorLocal = validarReserva(
                    nombre = nombre,
                    apellidos = apellidos,
                    dni = dni,
                    fechaNacimiento = fechaNacimiento,
                    correo = correo,
                    telefono = telefono,
                    semanas = semanasSeleccionadas
                )

                if (errorLocal == null) {
                    reservaViewModel.crearReserva(
                        token = token,
                        reserva = ReservaCreateRequest(
                            numeroSesiones = numeroSesiones,
                            nombre = nombre,
                            apellidos = apellidos,
                            dni = dni,
                            fechaNacimiento = normalizarFecha(fechaNacimiento),
                            correo = correo,
                            telefono = telefono.trim(),
                            club = club.ifBlank { null },
                            categoria = categoria.ifBlank { null },
                            codigoDescuento = codigoDescuento.ifBlank { null },
                            semanas = semanasSeleccionadas
                        )
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(130.dp)
        ) {
            Text("Enviar", color = textColor)
        }

        if (loading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(
                color = primaryColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        val mensajeError = errorLocal ?: error
        if (mensajeError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensajeError, color = textColor)
        }

        if (reservaCreada != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Reserva enviada correctamente. Total a pagar: ${reservaCreada!!.precioFinal} €",
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {
                showExitDialog = false
            },
            title = {
                Text("Salir de la reserva")
            },
            text = {
                Text("¿Estás seguro? Los cambios no se guardarán.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onBackToReservasInicio()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = backgroundColor,
            titleContentColor = textColor,
            textContentColor = textColor
        )
    }

    if (showBirthDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showBirthDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = reservaFormatDateMillis(millis)
                            if (hasAtLeast16Years(selectedDate)) {
                                fechaNacimiento = selectedDate
                                errorLocal = null
                            } else {
                                errorLocal = "Debes tener al menos 16 anos"
                            }
                        }
                        showBirthDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = secondaryColor)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservaSelectorSesiones(
    value: Int,
    opciones: List<Int>,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val opcionesFinales = if (opciones.isEmpty()) listOf(1) else opciones

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Número de sesiones:",
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.width(96.dp)
        ) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = textColor)
                },
                colors = reservaTextFieldColors(),
                shape = RoundedCornerShape(50),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesFinales.forEach { sesiones ->
                    DropdownMenuItem(
                        text = { Text("$sesiones") },
                        onClick = {
                            onValueChange(sesiones)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RellenarDatosUsuarioButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = secondaryColor,
            disabledContainerColor = Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(46.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = textColor,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text("Rellenar con mis datos", color = textColor)
        }
    }
}

@Composable
private fun ReservaInput(
    label: String,
    value: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = reservaTextFieldColors(),
            shape = RoundedCornerShape(50),
            modifier = Modifier.weight(1.25f)
        )
    }
}

@Composable
private fun ReservaDateInput(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.9f)
        )

        Box(
            modifier = Modifier
                .weight(1.25f)
                .clickable { onClick() }
        ) {
            OutlinedTextField(
                value = value.toDisplayDate(),
                onValueChange = {},
                placeholder = { Text("dd/mm/aaaa") },
                singleLine = true,
                enabled = false,
                readOnly = true,
                colors = reservaTextFieldColors(),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ReservaStepper(
    label: String,
    value: Int,
    min: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.9f)
        )

        Row(
            modifier = Modifier
                .weight(1.25f)
                .border(BorderStroke(2.dp, primaryColor), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onValueChange((value - 1).coerceAtLeast(min)) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("-", color = textColor)
            }

            Text("$value", color = textColor)

            TextButton(
                onClick = { onValueChange((value + 1).coerceAtMost(max)) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("+", color = textColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SemanaSeleccionadaRow(
    label: String,
    seleccion: SemanaSeleccionada,
    opciones: List<SemanaReserva>,
    onSemanaChange: (SemanaReserva) -> Unit,
    onHorarioClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.9f)
        )

        Row(
            modifier = Modifier.weight(1.25f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = seleccion.semana.rango,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = textColor)
                    },
                    colors = reservaTextFieldColors(),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opciones.forEach { semana ->
                        DropdownMenuItem(
                            text = { Text(semana.rango) },
                            onClick = {
                                onSemanaChange(semana)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clickable { onHorarioClick() }
                    .border(BorderStroke(2.dp, primaryColor), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seleccion.horario.name,
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PrecioReservaBox(
    precioSinDescuento: Double?,
    descuentoAplicado: Double?,
    precioFinal: Double?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, primaryColor), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Precio a pagar sin descuento: ${precioSinDescuento?.let { "%.2f €".format(it) } ?: "-"}",
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        if ((descuentoAplicado ?: 0.0) > 0.0) {
            Text(
                text = "Descuento aplicado: ${"%.2f €".format(descuentoAplicado)}",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Precio a pagar con descuento: ${precioFinal?.let { "%.2f €".format(it) } ?: "-"}",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                text = "Precio a pagar con descuento: Sin descuento aplicado",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun reservaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = primaryColor,
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    disabledTextColor = textColor,
    cursorColor = primaryColor,
    focusedContainerColor = backgroundColor,
    unfocusedContainerColor = backgroundColor,
    disabledContainerColor = backgroundColor,
    focusedPlaceholderColor = textColor.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.6f),
    disabledPlaceholderColor = textColor.copy(alpha = 0.6f),
    disabledBorderColor = primaryColor
)

private fun validarReserva(
    nombre: String,
    apellidos: String,
    dni: String,
    fechaNacimiento: String,
    correo: String,
    telefono: String,
    semanas: List<Int>
): String? {
    return when {
        nombre.isBlank() -> "Introduce el nombre"
        apellidos.isBlank() -> "Introduce los apellidos"
        dni.isBlank() -> "Introduce el DNI/NIE"
        fechaNacimiento.isBlank() -> "Introduce la fecha de nacimiento"
        !hasAtLeast16Years(normalizarFecha(fechaNacimiento)) -> "Debes tener al menos 16 anos"
        correo.isBlank() -> "Introduce el correo electrónico"
        telefono.isBlank() -> "Introduce el telefono"
        semanas.isEmpty() -> "Selecciona al menos una semana"
        semanas.distinct().size != semanas.size -> "Selecciona combinaciones de semana y horario distintas"
        else -> null
    }
}

private fun normalizarFecha(fecha: String): String {
    val partes = fecha.split("/", "-")

    return if (partes.size == 3 && partes[0].length == 2) {
        val dia = partes[0]
        val mes = partes[1]
        val anio = partes[2]
        "$anio-$mes-$dia"
    } else {
        fecha
    }
}

private fun reservaFormatDateMillis(millis: Long): String {
    val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    return String.format(
        java.util.Locale.ROOT,
        "%04d-%02d-%02d",
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH) + 1,
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )
}

private fun String.toDisplayDate(): String {
    return if (matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
        val parts = split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        this
    }
}

private fun hasAtLeast16Years(date: String): Boolean {
    val parts = date.split("-")
    if (parts.size != 3) {
        return false
    }

    val birthDate = java.util.Calendar.getInstance().apply {
        set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 0, 0, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }

    val limit = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.YEAR, -16)
    }

    return !birthDate.after(limit)
}

private fun siguienteSemanaDisponible(
    opciones: List<SemanaReserva>,
    seleccionadas: List<SemanaSeleccionada>
): SemanaSeleccionada {
    val idsUsados = seleccionadas.map { it.idSemana }.toSet()

    opciones.forEach { semana ->
        HorarioSemana.values().forEach { horario ->
            val seleccion = SemanaSeleccionada(semana, horario)
            if (seleccion.idSemana !in idsUsados) {
                return seleccion
            }
        }
    }

    return SemanaSeleccionada(opciones.first(), HorarioSemana.AM)
}

private fun List<SemanaSeleccionada>.idsSeleccionadosExcepto(index: Int): Set<Int> {
    return mapIndexedNotNull { itemIndex, seleccion ->
        if (itemIndex == index) null else seleccion.idSemana
    }.toSet()
}

private fun SemanaSeleccionada.evitarDuplicado(idsUsados: Set<Int>): SemanaSeleccionada? {
    if (idSemana !in idsUsados) {
        return this
    }

    val alternativa = copy(horario = horario.toggle())
    return alternativa.takeIf { it.idSemana !in idsUsados }
}

private data class SemanaReserva(
    val idAm: Int,
    val idPm: Int,
    val rango: String
)

private data class SemanaSeleccionada(
    val semana: SemanaReserva,
    val horario: HorarioSemana
) {
    val idSemana: Int
        get() = if (horario == HorarioSemana.AM) semana.idAm else semana.idPm
}

private enum class HorarioSemana {
    AM,
    PM;

    fun toggle(): HorarioSemana = if (this == AM) PM else AM
}
