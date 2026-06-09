package com.example.labball_tfg.Vistas.Cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.Modelo.EntrenamientoResponse
import com.example.labball_tfg.ViewModel.CLIENTE.EntrenamientoViewModel
import com.example.labball_tfg.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MisSesionesScreen(
    token: String,
    onAddSessionClick: () -> Unit = {},
    viewModel: EntrenamientoViewModel = viewModel()
) {
    val entrenamientos by viewModel.entrenamientos.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    var verTodas by remember { mutableStateOf(false) }
    var entrenamientoDialog by remember { mutableStateOf<EntrenamientoResponse?>(null) }
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val entrenamientosPendientes = remember(entrenamientos, currentTimeMillis) {
        entrenamientos.filter { entrenamiento ->
            isSesionPendiente(entrenamiento.horaInicio, currentTimeMillis)
        }
    }

    LaunchedEffect(token) {
        viewModel.cargarMisEntrenamientos(token)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        when {
            loading -> {
                CircularProgressIndicator(
                    color = primaryColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            error != null -> {
                Text(
                    text = error ?: "",
                    color = errorColor,
                    modifier = Modifier.align(Alignment.Center)
                )

            }

            else -> {
                SesionesTable(
                    entrenamientos = if (verTodas) entrenamientosPendientes else entrenamientosPendientes.take(4),
                    totalEntrenamientos = entrenamientosPendientes.size,
                    verTodas = verTodas,
                    onToggleVerMasClick = { verTodas = !verTodas },
                    onEntrenamientoClick = { entrenamientoDialog = it },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

            }
        }

        Button(
            onClick = onAddSessionClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
                contentColor = textColor
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Añadir sesión")

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Añadir sesión"
            )
        }
    }

    entrenamientoDialog?.let { entrenamiento ->
        AlertDialog(
            onDismissRequest = { entrenamientoDialog = null },
            containerColor = backgroundColor,
            title = {
                Text(
                    text = "Detalle de sesión",
                    color = textColor
                )
            },
            text = {
                Column {
                    Text("Entrenador: ${entrenamiento.nombreEntrenador}", color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ubicación: ${entrenamiento.ubicacion}", color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Inicio: ${formatFechaHora(entrenamiento.horaInicio)}", color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fin: ${formatFechaHora(entrenamiento.horaFin)}", color = textColor)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { entrenamientoDialog = null }
                ) {
                    Text("Cerrar", color = primaryColor)
                }
            }
        )
    }
}

@Composable
private fun SesionesTable(
    entrenamientos: List<EntrenamientoResponse>,
    totalEntrenamientos: Int,
    verTodas: Boolean,
    onToggleVerMasClick: () -> Unit,
    onEntrenamientoClick: (EntrenamientoResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholderRows = (4 - entrenamientos.size).coerceAtLeast(0)
    val rowHeight = if (totalEntrenamientos <= 4) 91.dp else 72.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp)
            .background(
                color = secondaryColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 12.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Entrenador",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1.5f)
            )

            Text(
                text = "Día",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1.05f)
            )

            Text(
                text = "Hora",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(0.8f)
            )

        }

        HorizontalDivider(
            color = textColor,
            thickness = 3.dp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        {
            items(entrenamientos) { entrenamiento ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntrenamientoClick(entrenamiento) }
                        .height(rowHeight)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entrenamiento.nombreEntrenador,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.5f)
                    )

                    Text(
                        text = formatFecha(entrenamiento.horaInicio),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.05f)
                    )

                    Text(
                        text = formatHora(entrenamiento.horaInicio),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(0.8f)
                    )

                }

                HorizontalDivider(
                    color = textColor,
                    thickness = 3.dp
                )
            }

            repeat(placeholderRows) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(rowHeight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sin sesión programada",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(
                        color = textColor,
                        thickness = 3.dp
                    )
                }
            }
        }

        if (totalEntrenamientos > 4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleVerMasClick() }
                    .height(56.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (verTodas) "Ver menos" else "Ver más",
                    color = textColor,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = if (verTodas) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = if (verTodas) "Ver menos" else "Ver más",
                    tint = textColor
                )
            }
        }

    }
}

private fun formatFechaHora(value: String): String {
    return formatDateTime(value, "HH:mm - dd/MM/yyyy")
}

private fun formatFecha(value: String): String {
    return formatDateTime(value, "dd/MM/yyyy")
}

private fun formatHora(value: String): String {
    return formatDateTime(value, "HH:mm")
}

private fun formatDateTime(value: String, outputPattern: String): String {
    val date = parseDateTime(value) ?: return value
    return SimpleDateFormat(outputPattern, Locale.getDefault()).format(date)
}

private fun isSesionPendiente(value: String, currentTimeMillis: Long): Boolean {
    val sessionDate = parseDateTime(value) ?: return true
    return sessionDate.after(Date(currentTimeMillis))
}

private fun parseDateTime(value: String): Date? {
    val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
    )

    for (format in inputFormats) {
        try {
            val date = format.parse(value)
            if (date != null) {
                return date
            }
        } catch (_: Exception) {
        }
    }

    return null
}

