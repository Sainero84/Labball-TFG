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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.Modelo.ReservaAdminListItemResponse
import com.example.labball_tfg.Modelo.ReservaResponse
import com.example.labball_tfg.ViewModel.ADMIN.AdminReservasViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.util.Locale

@Composable
fun AdminReservasScreen(
    token: String,
    viewModel: AdminReservasViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val entrenadores by viewModel.entrenadores.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val reservaSeleccionada by viewModel.reservaSeleccionada.collectAsState()
    val entrenamientosReserva by viewModel.entrenamientosReserva.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedReservaId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var reservaPendientePago by remember { mutableStateOf<ReservaAdminListItemResponse?>(null) }
    var mostrandoAsignarEntrenamiento by rememberSaveable { mutableStateOf(false) }
    var mostrandoEditarEntrenamiento by rememberSaveable { mutableStateOf(false) }

    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LaunchedEffect(token) {
        viewModel.cargarReservas(token)
        viewModel.cargarCatalogos(token)
        selectedReservaId?.let { idReserva ->
            viewModel.cargarReservaPorId(token, idReserva)
        }
    }

    BackHandler(enabled = selectedReservaId != null) {
        selectedReservaId = null
        viewModel.limpiarReservaSeleccionada()
    }

    val reservasFiltradas = remember(reservas, searchText, selectedTab) {
        reservas.filter { reserva ->
            val coincideBusqueda = adminReservaMatchesSearch(reserva, searchText)
            val estaAsignada = adminReservaTieneEntrenamientoAsignado(reserva)

            coincideBusqueda && if (selectedTab == 0) !estaAsignada else estaAsignada
        }
    }
    if ((mostrandoAsignarEntrenamiento || mostrandoEditarEntrenamiento) && reservaSeleccionada != null) {
        AdminAsignarEntrenamientoScreen(
            reserva = reservaSeleccionada!!,
            idJugador = reservaSeleccionada!!.idJugador,
            entrenamientosIniciales = if (mostrandoEditarEntrenamiento) entrenamientosReserva else emptyList(),
            entrenadores = entrenadores,
            ubicaciones = ubicaciones,
            editMode = mostrandoEditarEntrenamiento,
            onBack = {
                mostrandoAsignarEntrenamiento = false
                mostrandoEditarEntrenamiento = false
                viewModel.limpiarEntrenamientosReserva()
            },
            onGuardarEntrenamientos = { entrenamientos ->
                val idReserva = reservaSeleccionada!!.idReserva
                val onSuccess = {
                    mostrandoAsignarEntrenamiento = false
                    mostrandoEditarEntrenamiento = false
                    selectedReservaId = null
                    selectedTab = 1
                    viewModel.limpiarEntrenamientosReserva()
                    viewModel.limpiarReservaSeleccionada()
                    viewModel.cargarReservas(token)
                }

                if (mostrandoEditarEntrenamiento) {
                    viewModel.editarEntrenamientos(
                        token = token,
                        idReserva = idReserva,
                        entrenamientos = entrenamientos,
                        onSuccess = onSuccess
                    )
                } else {
                    viewModel.asignarEntrenamientos(
                        token = token,
                        idReserva = idReserva,
                        entrenamientos = entrenamientos,
                        onSuccess = onSuccess
                    )
                }
            }
        )

        if (errorMessage != null) {
            AlertDialog(
                containerColor = backgroundColor,
                onDismissRequest = { viewModel.limpiarError() },
                title = {
                    Text(
                        text = "No se pudo guardar",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = textColor
                    )
                },
                confirmButton = {
                    TextButton( { viewModel.limpiarError() }) {
                        Text("Aceptar", color = secondaryColor)
                    }
                }
            )
        }

        return
    }

    if (selectedReservaId == null) {
        AdminReservasListContent(
            reservas = reservasFiltradas,
            selectedTab = selectedTab,
            searchText = searchText,
            isLoading = isLoading,
            errorMessage = errorMessage,
            listState = listState,
            onTabSelected = { selectedTab = it },
            onSearchTextChange = { searchText = it },
            onReservaClick = { reserva ->
                selectedReservaId = reserva.idReserva
                viewModel.limpiarReservaSeleccionada()
                viewModel.cargarReservaPorId(token, reserva.idReserva)
            },
            onPagoClick = { reserva ->
                reservaPendientePago = reserva
            }
        )
    } else {
        AdminReservaDetailContent(
            reserva = reservaSeleccionada,
            isLoading = isLoading,
            errorMessage = errorMessage,
            showAssignButton = selectedTab == 0,
            showEditButton = selectedTab == 1,
            assignEnabled = reservaSeleccionada?.pagado == true,
            onAsignarEntrenamientoClick = {
                mostrandoAsignarEntrenamiento = true
            },
            onEditarEntrenamientoClick = {
                reservaSeleccionada?.let { reserva ->
                    viewModel.cargarEntrenamientosReserva(
                        token = token,
                        idReserva = reserva.idReserva,
                        onSuccess = {
                            mostrandoEditarEntrenamiento = true
                        }
                    )
                }
            }
        )
    }


    reservaPendientePago?.let { reserva ->
        val nuevoEstado = !reserva.pagado

        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { reservaPendientePago = null },
            title = {
                Text(
                    text = "Cambiar estado",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Â¿Seguro que quieres marcar esta reserva como ${if (nuevoEstado) "pagada" else "pendiente"}?",
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.actualizarPagado(token, reserva.idReserva, nuevoEstado)
                        reservaPendientePago = null
                    }
                ) {
                    Text("Confirmar", color = secondaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { reservaPendientePago = null }) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }
}

@Composable
private fun AdminReservasListContent(
    reservas: List<ReservaAdminListItemResponse>,
    selectedTab: Int,
    searchText: String,
    isLoading: Boolean,
    errorMessage: String?,
    listState: LazyListState,
    onTabSelected: (Int) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onReservaClick: (ReservaAdminListItemResponse) -> Unit,
    onPagoClick: (ReservaAdminListItemResponse) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AdminReservasTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )

        AdminReservaSearchField(
            value = searchText,
            onValueChange = onSearchTextChange
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading && reservas.isEmpty()) {
                item { AdminReservaLoadingMessage("Cargando reservas...") }
            }

            if (errorMessage != null) {
                item { AdminReservaErrorMessage(errorMessage) }
            }

            if (!isLoading && reservas.isEmpty() && errorMessage == null) {
                item {
                    AdminReservaEmptyMessage(
                        if (selectedTab == 0) "No hay reservas sin asignar." else "No hay reservas asignadas."
                    )
                }
            }

            items(
                items = reservas,
                key = { it.idReserva }
            ) { reserva ->
                AdminReservaSummaryCard(
                    reserva = reserva,
                    onClick = { onReservaClick(reserva) },
                    onPagoClick = { onPagoClick(reserva) }
                )
            }
        }
    }
}

@Composable
private fun AdminReservasTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 10.dp)
    ) {
        AdminReservasTabButton(
            text = "Sin Asignar",
            selected = selectedTab == 0,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(0) }
        )

        AdminReservasTabButton(
            text = "Asignadas",
            selected = selectedTab == 1,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(1) }
        )
    }
}

@Composable
private fun AdminReservasTabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, secondaryColor)
            .background(if (selected) secondaryColor.copy(alpha = 0.16f) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) secondaryColor else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminReservaSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "BUSCAR:",
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
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
            .fillMaxWidth()
            .height(66.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun AdminReservaSummaryCard(
    reserva: ReservaAdminListItemResponse,
    onClick: () -> Unit,
    onPagoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, secondaryColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${reserva.nombre} ${reserva.apellidos}",
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = reserva.correo,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "NÂº Sesiones: ${reserva.numeroSesiones}",
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (reserva.pagado) secondaryColor else Color.Transparent)
                .border(2.dp, textColor, RoundedCornerShape(5.dp))
                .clickable { onPagoClick() }
        )
    }
}

@Composable
private fun AdminReservaDetailContent(
    reserva: ReservaResponse?,
    isLoading: Boolean,
    errorMessage: String?,
    showAssignButton: Boolean,
    showEditButton: Boolean,
    assignEnabled: Boolean,
    onAsignarEntrenamientoClick: () -> Unit,
    onEditarEntrenamientoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading && reserva == null -> AdminReservaLoadingMessage("Cargando reserva...")
            errorMessage != null && reserva == null -> AdminReservaErrorMessage(errorMessage)
            reserva != null -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AdminReservaDetailFields(reserva)
                AdminReservaWeeksSection(reserva)
                AdminReservaPriceSection(reserva)

                if (showAssignButton) {
                    AdminReservaActionButton(
                        text = "Asignar entrenamiento",
                        enabled = assignEnabled,
                        onClick = onAsignarEntrenamientoClick
                    )

                    if (!assignEnabled) {
                        Text(
                            text = "No se puede asignar entrenamiento hasta que la reserva este pagada.",
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showEditButton) {
                    AdminReservaActionButton(
                        text = "Editar entrenamientos",
                        enabled = true,
                        onClick = onEditarEntrenamientoClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminReservaActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = secondaryColor,
            disabledContainerColor = Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminReservaDetailFields(reserva: ReservaResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        AdminReservaDetailRow("Nombre:", reserva.nombre, "Apellidos:", reserva.apellidos)
        AdminReservaDetailRow(
            "DNI/NIE:",
            reserva.dni,
            "Fecha de Nacimiento:",
            adminReservaFormatDate(reserva.fechaNacimiento)
        )
        AdminReservaDetailRow(
            "Correo electrÃ³nico:",
            reserva.correo,
            "TelÃ©fono:",
            reserva.telefono ?: "-"
        )
        AdminReservaDetailRow("Club:", reserva.club ?: "-", "CategorÃ­a:", reserva.categoria ?: "-")
        AdminReservaDetailRow(
            "CÃ³digo de descuento:",
            reserva.codigoDescuento ?: "-",
            "Descuento:",
            adminReservaFormatDiscount(reserva)
        )
        AdminReservaDetailRow(
            "Sesiones:",
            reserva.numeroSesiones.toString(),
            "Estado:",
            if (reserva.pagado) "Pagado" else "Pendiente"
        )
    }
}

@Composable
private fun AdminReservaDetailRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AdminReservaDetailField(leftLabel, leftValue, Modifier.weight(1f))
        AdminReservaDetailField(rightLabel, rightValue, Modifier.weight(1f))
    }
}

@Composable
private fun AdminReservaDetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(
            text = value,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminReservaWeeksSection(reserva: ReservaResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            text = "Semanas disponibles:",
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        if (reserva.semanas.isEmpty()) {
            AdminSemanaChip("-", Modifier.fillMaxWidth())
        } else {
            reserva.semanas.forEach { idSemana ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdminSemanaChip(adminSemanaTexto(idSemana), Modifier.weight(1f))
                    AdminSemanaChip(adminSemanaHorario(idSemana), Modifier.width(64.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminSemanaChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, secondaryColor, RoundedCornerShape(50))
            .heightIn(min = 42.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AdminReservaPriceSection(reserva: ReservaResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Precio sin descuento = ${adminReservaFormatPrice(reserva.precioSinDescuento)}",
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(50))
                .border(1.dp, secondaryColor, RoundedCornerShape(50))
                .heightIn(min = 50.dp)
                .padding(horizontal = 20.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Precio Final = ${adminReservaFormatPrice(reserva.precioFinal)}",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminReservaLoadingMessage(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = secondaryColor)
        Text(text = text, color = textColor, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun AdminReservaErrorMessage(text: String) {
    Text(
        text = text,
        color = Color(0xFFFF8A80),
        fontSize = 13.sp,
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
private fun AdminReservaEmptyMessage(text: String) {
    Text(
        text = text,
        color = textColor,
        fontSize = 14.sp,
        modifier = Modifier.padding(10.dp)
    )
}

private fun adminReservaMatchesSearch(
    reserva: ReservaAdminListItemResponse,
    searchText: String
): Boolean {
    val query = searchText.trim().lowercase(Locale.getDefault())
    if (query.isBlank()) return true

    val nombreCompleto = "${reserva.nombre} ${reserva.apellidos}".lowercase(Locale.getDefault())
    return nombreCompleto.contains(query) ||
            reserva.nombre.lowercase(Locale.getDefault()).contains(query) ||
            reserva.apellidos.lowercase(Locale.getDefault()).contains(query)
}

private fun adminReservaTieneEntrenamientoAsignado(
    reserva: ReservaAdminListItemResponse
): Boolean {
    return reserva.tieneEntrenamientos
}

private fun adminSemanaTexto(idSemana: Int): String {
    return when (idSemana) {
        1, 2 -> "23/06 - 27/06"
        3, 4 -> "30/06 - 04/07"
        5, 6 -> "07/07 - 11/07"
        7, 8 -> "14/07 - 18/07"
        else -> "Semana $idSemana"
    }
}

private fun adminSemanaHorario(idSemana: Int): String {
    return when (idSemana) {
        1, 3, 5, 7 -> "AM"
        2, 4, 6, 8 -> "PM"
        else -> "-"
    }
}

private fun adminReservaFormatDate(value: String): String {
    val parts = value.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else value
}

private fun adminReservaFormatDiscount(reserva: ReservaResponse): String {
    if (reserva.precioSinDescuento <= 0.0) return "0%"
    val percent = (reserva.descuentoAplicado / reserva.precioSinDescuento) * 100
    return "${percent.toInt()}%"
}

private fun adminReservaFormatPrice(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "${value.toInt()}â‚¬"
    } else {
        "${String.format(Locale.US, "%.2f", value)}â‚¬"
    }
}

