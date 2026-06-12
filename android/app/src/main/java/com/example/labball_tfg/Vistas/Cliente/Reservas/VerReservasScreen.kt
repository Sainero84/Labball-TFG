package com.example.labball_tfg.Vistas.Cliente.Reservas

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.Modelo.ReservaResponse
import com.example.labball_tfg.ViewModel.CLIENTE.ReservaViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.util.Locale

// Renderiza la pantalla ver reservas screen y conecta sus acciones principales.
@Composable
fun VerReservasScreen(
    token: String,
    onBackToReservasInicio: () -> Unit,
    reservaViewModel: ReservaViewModel = viewModel()
) {
    val reservas by reservaViewModel.reservas.collectAsState()
    val reservaSeleccionada by reservaViewModel.reservaSeleccionada.collectAsState()
    val isLoading by reservaViewModel.isLoading.collectAsState()
    val errorMessage by reservaViewModel.errorMessage.collectAsState()

    var selectedReservaId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(token) {
        selectedReservaId = null
        reservaViewModel.limpiarReservaSeleccionada()
        reservaViewModel.cargarMisReservas(token)
    }

    BackHandler(enabled = selectedReservaId == null) {
        onBackToReservasInicio()
    }

    BackHandler(enabled = selectedReservaId != null) {
        selectedReservaId = null
        reservaViewModel.limpiarReservaSeleccionada()
    }

    if (selectedReservaId == null) {
        ReservasListContent(
            reservas = reservas,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onReservaClick = { reserva ->
                selectedReservaId = reserva.idReserva
                reservaViewModel.limpiarReservaSeleccionada()
                reservaViewModel.cargarReservaPorId(token, reserva.idReserva)
            }
        )
    } else {
        ReservaDetailContent(
            reserva = reservaSeleccionada,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }
}

// Encapsula la operacion reservas list content usada por la pantalla o el estado.
@Composable
private fun ReservasListContent(
    reservas: List<ReservaResponse>,
    isLoading: Boolean,
    errorMessage: String?,
    onReservaClick: (ReservaResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading && reservas.isEmpty()) {
            item { LoadingMessage("Cargando reservas...") }
        }

        if (errorMessage != null) {
            item { ErrorMessage(errorMessage) }
        }

        if (!isLoading && reservas.isEmpty() && errorMessage == null) {
            item { EmptyMessage("Todavía no tienes reservas.") }
        }

        items(
            items = reservas,
            key = { it.idReserva }
        ) { reserva ->
            ReservaSummaryCard(
                reserva = reserva,
                onClick = { onReservaClick(reserva) }
            )
        }
    }
}

// Encapsula la operacion reserva summary card usada por la pantalla o el estado.
@Composable
private fun ReservaSummaryCard(
    reserva: ReservaResponse,
    onClick: () -> Unit
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
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
                text = "Nº Sesiones: ${reserva.numeroSesiones}",
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
        )
    }
}

// Encapsula la operacion reserva detail content usada por la pantalla o el estado.
@Composable
private fun ReservaDetailContent(
    reserva: ReservaResponse?,
    isLoading: Boolean,
    errorMessage: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading && reserva == null -> LoadingMessage("Cargando reserva...")
            errorMessage != null && reserva == null -> ErrorMessage(errorMessage)
            reserva != null -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ReservaDetailFields(reserva)
                ReservaWeeksSection(reserva)
                ReservaPriceSection(reserva)
            }
        }
    }
}

// Encapsula la operacion reserva detail fields usada por la pantalla o el estado.
@Composable
private fun ReservaDetailFields(reserva: ReservaResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        DetailRow("Nombre:", reserva.nombre, "Apellidos:", reserva.apellidos)
        DetailRow("DNI/NIE:", reserva.dni, "Fecha de Nacimiento:", formatDate(reserva.fechaNacimiento))
        DetailRow("Correo electrónico:", reserva.correo, "Teléfono:", reserva.telefono ?: "-")
        DetailRow("Club:", reserva.club ?: "-", "Categoría:", reserva.categoria ?: "-")
        DetailRow("Código de descuento:", reserva.codigoDescuento ?: "-", "Descuento:", formatDiscount(reserva))
        DetailRow("Sesiones:", reserva.numeroSesiones.toString(), "Estado:", if (reserva.pagado) "Pagado" else "Pendiente")
    }
}

// Encapsula la operacion detail row usada por la pantalla o el estado.
@Composable
private fun DetailRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DetailField(leftLabel, leftValue, Modifier.weight(1f))
        DetailField(rightLabel, rightValue, Modifier.weight(1f))
    }
}

// Encapsula la operacion detail field usada por la pantalla o el estado.
@Composable
private fun DetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

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

// Encapsula la operacion reserva weeks section usada por la pantalla o el estado.
@Composable
private fun ReservaWeeksSection(reserva: ReservaResponse) {
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
            SemanaChip(text = "-", modifier = Modifier.fillMaxWidth())
        } else {
            reserva.semanas.forEach { idSemana ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SemanaChip(
                        text = semanaTexto(idSemana),
                        modifier = Modifier.weight(1f)
                    )

                    SemanaChip(
                        text = semanaHorario(idSemana),
                        modifier = Modifier.width(64.dp)
                    )
                }
            }
        }
    }
}

// Encapsula la operacion semana chip usada por la pantalla o el estado.
@Composable
private fun SemanaChip(
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
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Encapsula la operacion reserva price section usada por la pantalla o el estado.
@Composable
private fun ReservaPriceSection(reserva: ReservaResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Precio sin descuento = ${formatPrice(reserva.precioSinDescuento)}",
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
                text = "Precio Final = ${formatPrice(reserva.precioFinal)}",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Encapsula la operacion loading message usada por la pantalla o el estado.
@Composable
private fun LoadingMessage(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = secondaryColor)
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

// Encapsula la operacion error message usada por la pantalla o el estado.
@Composable
private fun ErrorMessage(text: String) {
    Text(
        text = text,
        color = Color(0xFFFF8A80),
        fontSize = 13.sp,
        modifier = Modifier.padding(10.dp)
    )
}

// Encapsula la operacion empty message usada por la pantalla o el estado.
@Composable
private fun EmptyMessage(text: String) {
    Text(
        text = text,
        color = textColor,
        fontSize = 14.sp,
        modifier = Modifier.padding(10.dp)
    )
}

// Encapsula la operacion semana texto usada por la pantalla o el estado.
private fun semanaTexto(idSemana: Int): String {
    return when (idSemana) {
        1, 2 -> "23/06 - 27/06"
        3, 4 -> "30/06 - 04/07"
        5, 6 -> "07/07 - 11/07"
        7, 8 -> "14/07 - 18/07"
        else -> "Semana $idSemana"
    }
}

// Encapsula la operacion semana horario usada por la pantalla o el estado.
private fun semanaHorario(idSemana: Int): String {
    return when (idSemana) {
        1, 3, 5, 7 -> "AM"
        2, 4, 6, 8 -> "PM"
        else -> "-"
    }
}

// Encapsula la operacion format date usada por la pantalla o el estado.
private fun formatDate(value: String): String {
    val parts = value.split("-")
    return if (parts.size == 3) {
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        value
    }
}

// Encapsula la operacion format discount usada por la pantalla o el estado.
private fun formatDiscount(reserva: ReservaResponse): String {
    if (reserva.precioSinDescuento <= 0.0) return "0%"
    val percent = (reserva.descuentoAplicado / reserva.precioSinDescuento) * 100
    return "${percent.toInt()}%"
}

// Encapsula la operacion format price usada por la pantalla o el estado.
private fun formatPrice(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "${value.toInt()}€"
    } else {
        "${String.format(Locale.US, "%.2f", value)}€"
    }
}
