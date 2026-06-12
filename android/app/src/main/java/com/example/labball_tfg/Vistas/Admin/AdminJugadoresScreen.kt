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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
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
import com.example.labball_tfg.Modelo.AdminUsuarioResponse
import com.example.labball_tfg.Modelo.JugadorCreateRequest
import com.example.labball_tfg.Modelo.JugadorResponse
import com.example.labball_tfg.ViewModel.ADMIN.AdminJugadoresViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.text.Normalizer
import java.util.Locale

// Renderiza la pantalla admin jugadores screen y conecta sus acciones principales.
@Composable
fun AdminJugadoresScreen(
    token: String,
    onJugadorClick: (JugadorResponse) -> Unit = {},
    viewModel: AdminJugadoresViewModel = viewModel()
) {
    val jugadores by viewModel.jugadores.collectAsState()
    val usuariosClientes by viewModel.usuariosClientes.collectAsState()
    val entrenadoresCatalogo by viewModel.entrenadores.collectAsState()
    val ubicacionesCatalogo by viewModel.ubicaciones.collectAsState()
    val jugadorSeleccionado by viewModel.jugadorSeleccionado.collectAsState()
    val jugadorGuardado by viewModel.jugadorGuardado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var selectedJugadorId by rememberSaveable { mutableStateOf<Int?>(null) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedEntrenador by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedUbicacion by rememberSaveable { mutableStateOf<String?>(null) }
    var showUserSelector by rememberSaveable { mutableStateOf(false) }
    var pendingUsuarioId by rememberSaveable { mutableStateOf<Int?>(null) }
    var openCreatedPlayerAfterSave by rememberSaveable { mutableStateOf(false) }
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LaunchedEffect(token) {
        viewModel.cargarJugadores(token)
        viewModel.cargarUsuariosClientes(token)
        viewModel.cargarCatalogos(token)
    }

    LaunchedEffect(jugadorGuardado?.idJugador, openCreatedPlayerAfterSave) {
        val jugadorCreado = jugadorGuardado
        if (openCreatedPlayerAfterSave && jugadorCreado != null) {
            openCreatedPlayerAfterSave = false
            showUserSelector = false
            pendingUsuarioId = null
            selectedJugadorId = jugadorCreado.idJugador
            viewModel.limpiarJugadorSeleccionado()
            viewModel.cargarJugadorPorId(token, jugadorCreado.idJugador)
            viewModel.limpiarEstado()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            openCreatedPlayerAfterSave = false
        }
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
        (entrenadoresCatalogo + jugadores.flatMap { it.assignedEntrenadorNames() })
            .distinct()
            .sortedBy { it.normalizedForSearch() }
    }

    val ubicaciones = remember(jugadores, ubicacionesCatalogo) {
        (ubicacionesCatalogo + jugadores.flatMap { it.assignedUbicacionNames() })
            .distinct()
            .sortedBy { it.normalizedForSearch() }
    }

    val jugadoresFiltrados = remember(jugadores, searchText, selectedEntrenador, selectedUbicacion) {
        val busqueda = searchText.normalizedForSearch()
        val entrenadorSeleccionado = selectedEntrenador
        val ubicacionSeleccionada = selectedUbicacion

        jugadores.filter { jugador ->
            val coincideBusqueda = busqueda.isBlank() ||
                "${jugador.nombre} ${jugador.apellidos}"
                    .normalizedForSearch()
                    .contains(busqueda)

            val coincideEntrenador = entrenadorSeleccionado == null ||
                jugador.matchesEntrenador(entrenadorSeleccionado)

            val coincideUbicacion = ubicacionSeleccionada == null ||
                jugador.matchesUbicacion(ubicacionSeleccionada)

            coincideBusqueda && coincideEntrenador && coincideUbicacion
        }
    }

    val usuariosDisponiblesParaJugador = remember(usuariosClientes, jugadores) {
        val jugadoresUsuarioIds = jugadores.map { it.idUsuario }.toSet()
        usuariosClientes
            .filter { usuario -> usuario.idUsuario !in jugadoresUsuarioIds }
            .sortedBy { it.displayNameForPlayer().normalizedForSearch() }
    }

    val pendingUsuario = remember(pendingUsuarioId, usuariosClientes) {
        usuariosClientes.firstOrNull { it.idUsuario == pendingUsuarioId }
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
        successMessage = successMessage,
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
        },
        onAddPlayerClick = {
            viewModel.limpiarEstado()
            showUserSelector = true
        }
    )

    if (showUserSelector) {
        AdminJugadorUserSelectorDialog(
            usuarios = usuariosDisponiblesParaJugador,
            isLoading = isLoading,
            onUserSelected = { usuario ->
                pendingUsuarioId = usuario.idUsuario
                showUserSelector = false
            },
            onDismiss = { showUserSelector = false }
        )
    }

    pendingUsuario?.let { usuario ->
        AdminJugadorCreateConfirmDialog(
            usuario = usuario,
            isLoading = isLoading,
            onConfirm = {
                openCreatedPlayerAfterSave = true
                pendingUsuarioId = null
                viewModel.crearJugador(
                    token = token,
                    jugador = usuario.toJugadorCreateRequest()
                )
            },
            onDismiss = {
                openCreatedPlayerAfterSave = false
                pendingUsuarioId = null
            }
        )
    }
}

// Encapsula la operacion admin jugadores list content usada por la pantalla o el estado.
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
    successMessage: String?,
    listState: LazyListState,
    onSearchTextChange: (String) -> Unit,
    onEntrenadorSelected: (String?) -> Unit,
    onUbicacionSelected: (String?) -> Unit,
    onJugadorClick: (JugadorResponse) -> Unit,
    onAddPlayerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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

                if (successMessage != null) {
                    item {
                        AdminJugadoresMessage(successMessage)
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

        Button(
            onClick = onAddPlayerClick,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
                disabledContainerColor = secondaryColor.copy(alpha = 0.35f)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 18.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Anadir jugador",
                color = textColor
            )
        }
    }
}

// Encapsula la operacion admin jugadores search field usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugadores filters usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugadores filter selector usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugador summary card usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugador avatar usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugador metadata usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugadores loading message usada por la pantalla o el estado.
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

// Encapsula la operacion admin jugadores message usada por la pantalla o el estado.
@Composable
private fun AdminJugadoresMessage(message: String) {
    Text(
        text = message,
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 24.dp)
    )
}

// Encapsula la operacion admin jugador user selector dialog usada por la pantalla o el estado.
@Composable
private fun AdminJugadorUserSelectorDialog(
    usuarios: List<AdminUsuarioResponse>,
    isLoading: Boolean,
    onUserSelected: (AdminUsuarioResponse) -> Unit,
    onDismiss: () -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val usuariosFiltrados = remember(usuarios, searchText) {
        val query = searchText.normalizedForSearch()
        if (query.isBlank()) {
            usuarios
        } else {
            usuarios.filter { it.matchesPlayerUserSearch(query) }
        }
    }

    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar usuario",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar cliente") },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = adminJugadorUserTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    when {
                        isLoading && usuarios.isEmpty() -> {
                            CircularProgressIndicator(
                                color = secondaryColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        usuariosFiltrados.isEmpty() -> {
                            Text(
                                text = "No hay clientes disponibles sin jugador.",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 18.dp)
                            )
                        }

                        else -> {
                            LazyColumn {
                                items(
                                    items = usuariosFiltrados,
                                    key = { it.idUsuario }
                                ) { usuario ->
                                    AdminJugadorUserRow(
                                        usuario = usuario,
                                        onClick = { onUserSelected(usuario) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = textColor)
            }
        }
    )
}

// Encapsula la operacion admin jugador user row usada por la pantalla o el estado.
@Composable
private fun AdminJugadorUserRow(
    usuario: AdminUsuarioResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdminJugadorUserAvatar(usuario)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.displayNameForPlayer(),
                color = textColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = usuario.correo,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    HorizontalDivider(color = secondaryColor.copy(alpha = 0.4f))
}

// Encapsula la operacion admin jugador user avatar usada por la pantalla o el estado.
@Composable
private fun AdminJugadorUserAvatar(usuario: AdminUsuarioResponse) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(textColor.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        if (!usuario.fotoPerfilUrl.isNullOrBlank()) {
            AsyncImage(
                model = usuario.fotoPerfilUrl,
                contentDescription = "Foto de usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = secondaryColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// Encapsula la operacion admin jugador create confirm dialog usada por la pantalla o el estado.
@Composable
private fun AdminJugadorCreateConfirmDialog(
    usuario: AdminUsuarioResponse,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Crear jugador",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Se asociara un jugador a esta cuenta:",
                    color = textColor
                )
                Text(
                    text = usuario.correo,
                    color = secondaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Despues podras completar y editar sus datos.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                Text("Confirmar", color = secondaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar", color = textColor)
            }
        }
    )
}

// Encapsula la operacion admin jugador user text field colors usada por la pantalla o el estado.
@Composable
private fun adminJugadorUserTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = secondaryColor,
    focusedBorderColor = secondaryColor,
    unfocusedBorderColor = secondaryColor,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedPlaceholderColor = textColor.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.55f)
)

// Encapsula la operacion string usada por la pantalla o el estado.
private fun String.normalizedForSearch(): String {
    return Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.getDefault())
        .trim()
}

// Encapsula la operacion format altura usada por la pantalla o el estado.
private fun formatAltura(altura: Double?): String {
    return altura?.let {
        String.format(Locale("es", "ES"), "%.2f m", it)
    } ?: "- m"
}

// Encapsula la operacion format peso usada por la pantalla o el estado.
private fun formatPeso(peso: Double?): String {
    return peso?.let {
        if (it % 1.0 == 0.0) {
            "${it.toInt()} kg"
        } else {
            String.format(Locale("es", "ES"), "%.1f kg", it)
        }
    } ?: "- kg"
}

// Encapsula la operacion jugador response usada por la pantalla o el estado.
private fun JugadorResponse.trainingSummary(): String {
    val entrenador = assignedEntrenadorNames().firstOrNull()
    val ubicacion = assignedUbicacionNames().firstOrNull()

    return listOfNotNull(
        entrenador,
        ubicacion
    ).joinToString(" - ")
}

// Encapsula la operacion jugador response usada por la pantalla o el estado.
private fun JugadorResponse.assignedEntrenadorNames(): List<String> {
    return (entrenadores.map { it.nombre } + listOfNotNull(nombreEntrenador))
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.normalizedForSearch() }
}

// Encapsula la operacion jugador response usada por la pantalla o el estado.
private fun JugadorResponse.assignedUbicacionNames(): List<String> {
    return (ubicaciones.map { it.nombre } + listOfNotNull(ubicacion))
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.normalizedForSearch() }
}

// Encapsula la operacion jugador response usada por la pantalla o el estado.
private fun JugadorResponse.matchesEntrenador(selectedEntrenador: String): Boolean {
    val selected = selectedEntrenador.normalizedForSearch()
    return assignedEntrenadorNames().any { it.normalizedForSearch() == selected }
}

// Encapsula la operacion jugador response usada por la pantalla o el estado.
private fun JugadorResponse.matchesUbicacion(selectedUbicacion: String): Boolean {
    val selected = selectedUbicacion.normalizedForSearch()
    return assignedUbicacionNames().any { it.normalizedForSearch() == selected }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.displayNameForPlayer(): String {
    return listOfNotNull(
        nombre?.trim()?.takeIf { it.isNotBlank() },
        apellido1?.trim()?.takeIf { it.isNotBlank() }
    ).joinToString(" ").ifBlank {
        correo.substringBefore("@").takeIf { it.isNotBlank() } ?: correo
    }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.matchesPlayerUserSearch(query: String): Boolean {
    return listOf(
        correo,
        telefono.orEmpty(),
        displayNameForPlayer()
    ).any { it.normalizedForSearch().contains(query) }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.toJugadorCreateRequest(): JugadorCreateRequest {
    return JugadorCreateRequest(
        idUsuario = idUsuario,
        nombre = nombre?.trim()?.takeIf { it.isNotBlank() }
            ?: correo.substringBefore("@").takeIf { it.isNotBlank() }
            ?: "Jugador",
        apellidos = apellido1?.trim()?.takeIf { it.isNotBlank() } ?: "Sin apellidos"
    )
}


