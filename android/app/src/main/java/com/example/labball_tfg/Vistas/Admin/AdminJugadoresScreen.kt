package com.example.labball_tfg.Vistas.Admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.labball_tfg.Modelo.JugadorResponse
import com.example.labball_tfg.ViewModel.ADMIN.AdminJugadoresViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.text.Normalizer
import java.util.Locale

@Composable
fun AdminJugadoresScreen(
    token: String,
    onJugadorClick: (JugadorResponse) -> Unit = {},
    viewModel: AdminJugadoresViewModel = viewModel()
) {
    val jugadores by viewModel.jugadores.collectAsState()
    val entrenadoresCatalogo by viewModel.entrenadores.collectAsState()
    val ubicacionesCatalogo by viewModel.ubicaciones.collectAsState()
    val jugadorSeleccionado by viewModel.jugadorSeleccionado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedJugadorId by rememberSaveable { mutableStateOf<Int?>(null) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedEntrenador by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedUbicacion by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LaunchedEffect(token) {
        viewModel.cargarJugadores(token)
        viewModel.cargarCatalogos(token)
    }

    BackHandler(enabled = selectedJugadorId != null) {
        selectedJugadorId = null
        viewModel.limpiarJugadorSeleccionado()
        viewModel.limpiarEstado()
    }

    if (selectedJugadorId != null) {
        AdminJugadorDetailScreen(
            jugador = jugadorSeleccionado,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onBack = {
                selectedJugadorId = null
                viewModel.limpiarJugadorSeleccionado()
                viewModel.limpiarEstado()
            },
            onActualizarDatos = { jugador ->
                selectedJugadorId?.let { idJugador ->
                    viewModel.actualizarJugador(
                        token = token,
                        idJugador = idJugador,
                        jugador = jugador
                    )
                }
            },
            onActualizarEstadisticas = { estadisticas ->
                selectedJugadorId?.let { idJugador ->
                    viewModel.actualizarEstadisticas(
                        token = token,
                        idJugador = idJugador,
                        estadisticas = estadisticas
                    )
                }
            }
        )
        return
    }

    val entrenadores = remember(jugadores, entrenadoresCatalogo) {
        (entrenadoresCatalogo + jugadores.mapNotNull { it.nombreEntrenador?.takeIf { value -> value.isNotBlank() } })
            .distinct()
            .sortedBy { it.normalizedForSearch() }
    }

    val ubicaciones = remember(jugadores, ubicacionesCatalogo) {
        (ubicacionesCatalogo + jugadores.mapNotNull { it.ubicacion?.takeIf { value -> value.isNotBlank() } })
            .distinct()
            .sortedBy { it.normalizedForSearch() }
    }

    val jugadoresFiltrados = remember(jugadores, searchText, selectedEntrenador, selectedUbicacion) {
        val busqueda = searchText.normalizedForSearch()

        jugadores.filter { jugador ->
            val coincideBusqueda = busqueda.isBlank() ||
                "${jugador.nombre} ${jugador.apellidos}"
                    .normalizedForSearch()
                    .contains(busqueda)

            val coincideEntrenador = selectedEntrenador == null ||
                jugador.nombreEntrenador == selectedEntrenador

            val coincideUbicacion = selectedUbicacion == null ||
                jugador.ubicacion == selectedUbicacion

            coincideBusqueda && coincideEntrenador && coincideUbicacion
        }
    }

    AdminJugadoresListContent(
        jugadores = jugadoresFiltrados,
        searchText = searchText,
        entrenadores = entrenadores,
        ubicaciones = ubicaciones,
        selectedEntrenador = selectedEntrenador,
        selectedUbicacion = selectedUbicacion,
        isLoading = isLoading,
        errorMessage = errorMessage,
        listState = listState,
        onSearchTextChange = { searchText = it },
        onEntrenadorSelected = { selectedEntrenador = it },
        onUbicacionSelected = { selectedUbicacion = it },
        onJugadorClick = { jugador ->
            selectedJugadorId = jugador.idJugador
            viewModel.limpiarJugadorSeleccionado()
            viewModel.limpiarEstado()
            viewModel.cargarJugadorPorId(token, jugador.idJugador)
            onJugadorClick(jugador)
        }
    )
}

@Composable
private fun AdminJugadoresListContent(
    jugadores: List<JugadorResponse>,
    searchText: String,
    entrenadores: List<String>,
    ubicaciones: List<String>,
    selectedEntrenador: String?,
    selectedUbicacion: String?,
    isLoading: Boolean,
    errorMessage: String?,
    listState: LazyListState,
    onSearchTextChange: (String) -> Unit,
    onEntrenadorSelected: (String?) -> Unit,
    onUbicacionSelected: (String?) -> Unit,
    onJugadorClick: (JugadorResponse) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AdminJugadoresSearchField(
            value = searchText,
            onValueChange = onSearchTextChange
        )

        AdminJugadoresFilters(
            entrenadores = entrenadores,
            ubicaciones = ubicaciones,
            selectedEntrenador = selectedEntrenador,
            selectedUbicacion = selectedUbicacion,
            onEntrenadorSelected = onEntrenadorSelected,
            onUbicacionSelected = onUbicacionSelected
        )

        HorizontalDivider(
            color = secondaryColor,
            thickness = 1.dp
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            if (isLoading && jugadores.isEmpty()) {
                item {
                    AdminJugadoresLoadingMessage()
                }
            }

            if (errorMessage != null) {
                item {
                    AdminJugadoresMessage(errorMessage)
                }
            }

            if (!isLoading && jugadores.isEmpty() && errorMessage == null) {
                item {
                    AdminJugadoresMessage("No hay jugadores que mostrar.")
                }
            }

            items(
                items = jugadores,
                key = { it.idJugador }
            ) { jugador ->
                AdminJugadorSummaryCard(
                    jugador = jugador,
                    onClick = { onJugadorClick(jugador) }
                )
            }
        }
    }
}

@Composable
private fun AdminJugadoresSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Buscar:",
                color = Color.Gray,
                fontSize = 16.sp
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
            .padding(horizontal = 18.dp, vertical = 6.dp)
    )
}

@Composable
private fun AdminJugadoresFilters(
    entrenadores: List<String>,
    ubicaciones: List<String>,
    selectedEntrenador: String?,
    selectedUbicacion: String?,
    onEntrenadorSelected: (String?) -> Unit,
    onUbicacionSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdminJugadoresFilterSelector(
            label = "Entrenador",
            selectedValue = selectedEntrenador,
            options = entrenadores,
            modifier = Modifier.weight(1f),
            onSelected = onEntrenadorSelected
        )

        AdminJugadoresFilterSelector(
            label = "Ubicacion",
            selectedValue = selectedUbicacion,
            options = ubicaciones,
            modifier = Modifier.weight(1f),
            onSelected = onUbicacionSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminJugadoresFilterSelector(
    label: String,
    selectedValue: String?,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue ?: "Todos",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = textColor
                )
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = secondaryColor,
                focusedBorderColor = secondaryColor,
                unfocusedBorderColor = secondaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.65f)
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
            DropdownMenuItem(
                text = { Text("Todos", color = textColor) },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
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

@Composable
private fun AdminJugadorSummaryCard(
    jugador: JugadorResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdminJugadorAvatar(jugador)

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${jugador.nombre} ${jugador.apellidos}",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AdminJugadorMetadata(jugador.posicion ?: "Sin posicion")
                AdminJugadorMetadata(formatAltura(jugador.altura))
                AdminJugadorMetadata(formatPeso(jugador.peso))
            }

            val asignacion = jugador.trainingSummary()
            if (asignacion.isNotBlank()) {
                Text(
                    text = asignacion,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }

    HorizontalDivider(
        color = secondaryColor,
        thickness = 1.dp
    )
}

@Composable
private fun AdminJugadorAvatar(jugador: JugadorResponse) {
    Box(
        modifier = Modifier
            .size(58.dp)
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
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun AdminJugadorMetadata(value: String) {
    Text(
        text = value,
        color = Color.Gray,
        fontSize = 17.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AdminJugadoresLoadingMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = secondaryColor)
    }
}

@Composable
private fun AdminJugadoresMessage(message: String) {
    Text(
        text = message,
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 24.dp)
    )
}

private fun String.normalizedForSearch(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.getDefault())
        .trim()
}

private fun formatAltura(altura: Double?): String {
    return altura?.let {
        String.format(Locale("es", "ES"), "%.2f m", it)
    } ?: "- m"
}

private fun formatPeso(peso: Double?): String {
    return peso?.let {
        if (it % 1.0 == 0.0) {
            "${it.toInt()} kg"
        } else {
            String.format(Locale("es", "ES"), "%.1f kg", it)
        }
    } ?: "- kg"
}

private fun JugadorResponse.trainingSummary(): String {
    return listOfNotNull(
        nombreEntrenador?.takeIf { it.isNotBlank() },
        ubicacion?.takeIf { it.isNotBlank() }
    ).joinToString(" - ")
}


