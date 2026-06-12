package com.example.labball_tfg.Vistas.Admin

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.labball_tfg.Modelo.AdminUsuarioResponse
import com.example.labball_tfg.Modelo.DescuentoResponse
import com.example.labball_tfg.Modelo.UbicacionResponse
import com.example.labball_tfg.Modelo.UsuarioMeResponse
import com.example.labball_tfg.ViewModel.ADMIN.AdminUsuariosViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.errorColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.util.Locale

// Renderiza la pantalla admin usuarios screen y conecta sus acciones principales.
@Composable
fun AdminUsuariosScreen(
    token: String,
    viewModel: AdminUsuariosViewModel = viewModel()
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val descuentos by viewModel.descuentos.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val usuarioGuardado by viewModel.usuarioGuardado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var selectedTabName by rememberSaveable { mutableStateOf(AdminNegocioTab.Usuarios.name) }
    var usuariosSearch by rememberSaveable { mutableStateOf("") }
    var descuentosSearch by rememberSaveable { mutableStateOf("") }
    var ubicacionesSearch by rememberSaveable { mutableStateOf("") }

    var showCreateUserDialog by rememberSaveable { mutableStateOf(false) }
    var createEmail by rememberSaveable { mutableStateOf("") }
    var createRoleName by rememberSaveable { mutableStateOf(AdminUsuarioRole.Cliente.name) }
    var createEntrenador by rememberSaveable { mutableStateOf(false) }
    var waitingForCreate by rememberSaveable { mutableStateOf(false) }

    var selectedUsuario by remember { mutableStateOf<AdminUsuarioResponse?>(null) }
    var selectedRoleName by rememberSaveable { mutableStateOf(AdminUsuarioRole.Cliente.name) }
    var selectedEntrenador by rememberSaveable { mutableStateOf(false) }
    var pendingRoleChange by remember { mutableStateOf<AdminUsuarioRoleChange?>(null) }
    var pendingUserDelete by remember { mutableStateOf<AdminUsuarioResponse?>(null) }
    var deleteUserEmail by rememberSaveable { mutableStateOf("") }

    var showDescuentoDialog by rememberSaveable { mutableStateOf(false) }
    var editingDescuento by remember { mutableStateOf<DescuentoResponse?>(null) }
    var descuentoCodigo by rememberSaveable { mutableStateOf("") }
    var descuentoPorcentaje by rememberSaveable { mutableStateOf("") }
    var pendingDescuentoDelete by remember { mutableStateOf<DescuentoResponse?>(null) }

    var showUbicacionDialog by rememberSaveable { mutableStateOf(false) }
    var ubicacionNombre by rememberSaveable { mutableStateOf("") }
    var pendingUbicacionDelete by remember { mutableStateOf<UbicacionResponse?>(null) }

    val isSuperAdmin = usuarioActual?.esSuperAdmin == true
    val availableTabs = if (isSuperAdmin) AdminNegocioTab.values().toList() else listOf(AdminNegocioTab.Usuarios)
    val selectedTab = AdminNegocioTab.fromName(selectedTabName).takeIf { it in availableTabs } ?: AdminNegocioTab.Usuarios

    LaunchedEffect(token) {
        viewModel.cargarNegocio(token)
    }

    LaunchedEffect(isSuperAdmin, selectedTabName) {
        if (!isSuperAdmin && selectedTabName != AdminNegocioTab.Usuarios.name) {
            selectedTabName = AdminNegocioTab.Usuarios.name
        }
    }

    LaunchedEffect(usuarioGuardado?.idUsuario, waitingForCreate) {
        if (waitingForCreate && usuarioGuardado != null) {
            showCreateUserDialog = false
            createEmail = ""
            createRoleName = AdminUsuarioRole.Cliente.name
            createEntrenador = false
            waitingForCreate = false
            viewModel.limpiarEstado()
        }
    }

    LaunchedEffect(errorMessage, waitingForCreate) {
        if (waitingForCreate && errorMessage != null) {
            waitingForCreate = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (isSuperAdmin) {
            AdminNegocioTabs(
                tabs = availableTabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTabName = it.name }
            )
        }

        when (selectedTab) {
            AdminNegocioTab.Usuarios -> AdminNegocioUsuariosContent(
                usuarios = usuarios.businessUserFilter(usuariosSearch),
                searchText = usuariosSearch,
                usuarioActual = usuarioActual,
                isLoading = isLoading,
                onSearchChange = { usuariosSearch = it },
                onCreateClick = {
                    createEmail = ""
                    createRoleName = AdminUsuarioRole.Cliente.name
                    createEntrenador = false
                    viewModel.limpiarEstado()
                    showCreateUserDialog = true
                },
                onManageClick = { usuario ->
                    selectedUsuario = usuario
                    selectedRoleName = usuario.role().name
                    selectedEntrenador = usuario.esEntrenador
                }
            )

            AdminNegocioTab.Descuentos -> AdminNegocioDescuentosContent(
                descuentos = descuentos.businessDiscountFilter(descuentosSearch),
                searchText = descuentosSearch,
                isLoading = isLoading,
                onSearchChange = { descuentosSearch = it },
                onCreateClick = {
                    editingDescuento = null
                    descuentoCodigo = ""
                    descuentoPorcentaje = ""
                    viewModel.limpiarEstado()
                    showDescuentoDialog = true
                },
                onEditClick = { descuento ->
                    editingDescuento = descuento
                    descuentoCodigo = descuento.codigo
                    descuentoPorcentaje = descuento.porcentaje.toString()
                    viewModel.limpiarEstado()
                    showDescuentoDialog = true
                },
                onDeleteClick = { pendingDescuentoDelete = it }
            )

            AdminNegocioTab.Ubicaciones -> AdminNegocioUbicacionesContent(
                ubicaciones = ubicaciones.businessLocationFilter(ubicacionesSearch),
                searchText = ubicacionesSearch,
                isLoading = isLoading,
                onSearchChange = { ubicacionesSearch = it },
                onCreateClick = {
                    ubicacionNombre = ""
                    viewModel.limpiarEstado()
                    showUbicacionDialog = true
                },
                onDeleteClick = { pendingUbicacionDelete = it }
            )
        }
    }

    if (showCreateUserDialog) {
        val createRole = AdminUsuarioRole.fromName(createRoleName)
        AdminUsuarioCreateDialog(
            email = createEmail,
            selectedRole = createRole,
            isTrainer = createEntrenador,
            availableRoles = usuarioActual.availableRoles(),
            canEditTrainer = isSuperAdmin,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onEmailChange = { createEmail = it },
            onRoleChange = { role ->
                createRoleName = role.name
                if (role == AdminUsuarioRole.Cliente) {
                    createEntrenador = false
                }
            },
            onTrainerChange = { createEntrenador = it },
            onConfirm = {
                waitingForCreate = true
                viewModel.crearUsuario(
                    token = token,
                    correo = createEmail,
                    esAdmin = createRole == AdminUsuarioRole.Admin,
                    esSuperAdmin = createRole == AdminUsuarioRole.SuperAdmin,
                    esEntrenador = createEntrenador
                )
            },
            onDismiss = {
                showCreateUserDialog = false
                waitingForCreate = false
                viewModel.limpiarEstado()
            }
        )
    }

    selectedUsuario?.let { usuario ->
        AdminUsuarioManageDialog(
            usuario = usuario,
            selectedRole = AdminUsuarioRole.fromName(selectedRoleName),
            isTrainer = selectedEntrenador,
            availableRoles = usuarioActual.availableRoles(),
            canEditTrainer = isSuperAdmin,
            canDelete = usuario.canBeDeletedBy(usuarioActual),
            onRoleChange = { role ->
                selectedRoleName = role.name
                if (role == AdminUsuarioRole.Cliente) {
                    selectedEntrenador = false
                }
            },
            onTrainerChange = { selectedEntrenador = it },
            onSaveClick = {
                selectedUsuario = null
                pendingRoleChange = AdminUsuarioRoleChange(
                    usuario = usuario,
                    role = AdminUsuarioRole.fromName(selectedRoleName),
                    esEntrenador = selectedEntrenador
                )
            },
            onDeleteClick = {
                selectedUsuario = null
                deleteUserEmail = ""
                pendingUserDelete = usuario
            },
            onDismiss = { selectedUsuario = null }
        )
    }

    pendingRoleChange?.let { change ->
        AdminUsuarioRoleConfirmDialog(
            roleChange = change,
            onConfirm = {
                viewModel.actualizarRol(
                    token = token,
                    idUsuario = change.usuario.idUsuario,
                    esAdmin = change.role == AdminUsuarioRole.Admin,
                    esSuperAdmin = change.role == AdminUsuarioRole.SuperAdmin,
                    esEntrenador = change.esEntrenador
                )
                pendingRoleChange = null
            },
            onDismiss = {
                pendingRoleChange = null
            }
        )
    }

    pendingUserDelete?.let { usuario ->
        AdminDeleteUserDialog(
            usuario = usuario,
            confirmationEmail = deleteUserEmail,
            onConfirmationEmailChange = { deleteUserEmail = it },
            onConfirm = {
                viewModel.eliminarUsuario(token, usuario.idUsuario)
                pendingUserDelete = null
                deleteUserEmail = ""
            },
            onDismiss = {
                pendingUserDelete = null
                deleteUserEmail = ""
            }
        )
    }

    if (showDescuentoDialog) {
        AdminDescuentoEditDialog(
            descuento = editingDescuento,
            codigo = descuentoCodigo,
            porcentaje = descuentoPorcentaje,
            isLoading = isLoading,
            onCodigoChange = { descuentoCodigo = it },
            onPorcentajeChange = { descuentoPorcentaje = it },
            onConfirm = {
                val descuento = editingDescuento
                if (descuento == null) {
                    viewModel.crearDescuento(token, descuentoCodigo, descuentoPorcentaje)
                } else {
                    viewModel.actualizarDescuento(
                        token = token,
                        idDescuento = descuento.idDescuento,
                        codigo = descuentoCodigo,
                        porcentajeTexto = descuentoPorcentaje
                    )
                }
                showDescuentoDialog = false
            },
            onDismiss = {
                showDescuentoDialog = false
                editingDescuento = null
                viewModel.limpiarEstado()
            }
        )
    }

    pendingDescuentoDelete?.let { descuento ->
        AdminSimpleDeleteDialog(
            title = "Eliminar descuento",
            text = "Quieres eliminar el descuento ${descuento.codigo}?",
            onConfirm = {
                viewModel.eliminarDescuento(token, descuento.idDescuento)
                pendingDescuentoDelete = null
            },
            onDismiss = { pendingDescuentoDelete = null }
        )
    }

    if (showUbicacionDialog) {
        AdminUbicacionDialog(
            nombre = ubicacionNombre,
            isLoading = isLoading,
            onNombreChange = { ubicacionNombre = it },
            onConfirm = {
                viewModel.crearUbicacion(token, ubicacionNombre)
                showUbicacionDialog = false
            },
            onDismiss = {
                showUbicacionDialog = false
                viewModel.limpiarEstado()
            }
        )
    }

    pendingUbicacionDelete?.let { ubicacion ->
        AdminSimpleDeleteDialog(
            title = "Eliminar ubicacion",
            text = "Quieres eliminar la ubicacion ${ubicacion.nombre}?",
            onConfirm = {
                viewModel.eliminarUbicacion(token, ubicacion.idUbicacion)
                pendingUbicacionDelete = null
            },
            onDismiss = { pendingUbicacionDelete = null }
        )
    }

    successMessage?.let { message ->
        AdminInfoDialog(
            title = "Correcto",
            text = message,
            onDismiss = { viewModel.limpiarEstado() }
        )
    }

    if (errorMessage != null && !showCreateUserDialog && !showDescuentoDialog && !showUbicacionDialog) {
        AdminInfoDialog(
            title = "Error",
            text = errorMessage.orEmpty(),
            onDismiss = { viewModel.limpiarEstado() },
            isError = true
        )
    }
}

// Encapsula la operacion admin negocio tabs usada por la pantalla o el estado.
@Composable
private fun AdminNegocioTabs(
    tabs: List<AdminNegocioTab>,
    selectedTab: AdminNegocioTab,
    onTabSelected: (AdminNegocioTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val selected = tab == selectedTab
            Button(
                onClick = { onTabSelected(tab) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) secondaryColor else Color.Transparent,
                    contentColor = textColor
                ),
                border = BorderStroke(1.dp, secondaryColor),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tab.label,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Encapsula la operacion admin negocio usuarios content usada por la pantalla o el estado.
@Composable
private fun AdminNegocioUsuariosContent(
    usuarios: List<AdminUsuarioResponse>,
    searchText: String,
    usuarioActual: UsuarioMeResponse?,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onManageClick: (AdminUsuarioResponse) -> Unit
) {
    AdminNegocioListLayout(
        searchText = searchText,
        searchPlaceholder = "Buscar usuarios",
        actionText = "Crear usuario",
        isLoading = isLoading,
        emptyText = "No hay usuarios que mostrar.",
        itemCount = usuarios.size,
        onSearchChange = onSearchChange,
        onActionClick = onCreateClick
    ) {
        items(items = usuarios, key = { it.idUsuario }) { usuario ->
            AdminUsuarioCard(
                usuario = usuario,
                enabled = usuario.canBeManagedBy(usuarioActual),
                onManageClick = { onManageClick(usuario) }
            )
        }
    }
}

// Encapsula la operacion admin negocio descuentos content usada por la pantalla o el estado.
@Composable
private fun AdminNegocioDescuentosContent(
    descuentos: List<DescuentoResponse>,
    searchText: String,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (DescuentoResponse) -> Unit,
    onDeleteClick: (DescuentoResponse) -> Unit
) {
    AdminNegocioListLayout(
        searchText = searchText,
        searchPlaceholder = "Buscar descuentos",
        actionText = "Crear descuento",
        isLoading = isLoading,
        emptyText = "No hay descuentos que mostrar.",
        itemCount = descuentos.size,
        onSearchChange = onSearchChange,
        onActionClick = onCreateClick
    ) {
        items(items = descuentos, key = { it.idDescuento }) { descuento ->
            AdminDescuentoCard(
                descuento = descuento,
                onEditClick = { onEditClick(descuento) },
                onDeleteClick = { onDeleteClick(descuento) }
            )
        }
    }
}

// Encapsula la operacion admin negocio ubicaciones content usada por la pantalla o el estado.
@Composable
private fun AdminNegocioUbicacionesContent(
    ubicaciones: List<UbicacionResponse>,
    searchText: String,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteClick: (UbicacionResponse) -> Unit
) {
    AdminNegocioListLayout(
        searchText = searchText,
        searchPlaceholder = "Buscar ubicaciones",
        actionText = "Anadir ubicacion",
        isLoading = isLoading,
        emptyText = "No hay ubicaciones que mostrar.",
        itemCount = ubicaciones.size,
        onSearchChange = onSearchChange,
        onActionClick = onCreateClick
    ) {
        items(items = ubicaciones, key = { it.idUbicacion }) { ubicacion ->
            AdminUbicacionCard(
                ubicacion = ubicacion,
                onDeleteClick = { onDeleteClick(ubicacion) }
            )
        }
    }
}

// Encapsula la operacion admin negocio list layout usada por la pantalla o el estado.
@Composable
private fun AdminNegocioListLayout(
    searchText: String,
    searchPlaceholder: String,
    actionText: String,
    isLoading: Boolean,
    emptyText: String,
    itemCount: Int,
    onSearchChange: (String) -> Unit,
    onActionClick: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AdminBusinessSearchField(
            value = searchText,
            placeholder = searchPlaceholder,
            onValueChange = onSearchChange
        )

        Button(
            onClick = onActionClick,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor,
                contentColor = textColor
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        ) {
            Text(actionText)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.Add, contentDescription = null)
        }

        HorizontalDivider(color = secondaryColor, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading && itemCount == 0) item { AdminBusinessLoading() }
            if (!isLoading && itemCount == 0) item { AdminBusinessMessage(emptyText) }
            content()
        }
    }
}

// Encapsula la operacion admin business search field usada por la pantalla o el estado.
@Composable
private fun AdminBusinessSearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray, fontSize = 16.sp) },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = adminUsuarioTextFieldColors(),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    )
}

// Encapsula la operacion admin usuario card usada por la pantalla o el estado.
@Composable
private fun AdminUsuarioCard(
    usuario: AdminUsuarioResponse,
    enabled: Boolean,
    onManageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, secondaryColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdminUsuarioAvatar(usuario)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.correo,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = usuario.displayName().ifBlank { usuario.telefono ?: "Sin telefono" },
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = usuario.roleLabel(),
                color = if (usuario.role() == AdminUsuarioRole.Cliente) secondaryColor else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (enabled) {
            IconButton(onClick = onManageClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Gestionar usuario", tint = secondaryColor)
            }
        } else {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Usuario protegido",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(24.dp)
            )
        }
    }
}

// Encapsula la operacion admin usuario avatar usada por la pantalla o el estado.
@Composable
private fun AdminUsuarioAvatar(usuario: AdminUsuarioResponse) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(textColor.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        if (!usuario.fotoPerfilUrl.isNullOrBlank()) {
            AsyncImage(
                model = usuario.fotoPerfilUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(Icons.Filled.Person, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(30.dp))
        }
    }
}

// Encapsula la operacion admin descuento card usada por la pantalla o el estado.
@Composable
private fun AdminDescuentoCard(
    descuento: DescuentoResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AdminBusinessCard {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = descuento.codigo,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "${descuento.porcentaje}%", color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Filled.Edit, contentDescription = "Editar descuento", tint = secondaryColor)
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar descuento", tint = errorColor)
        }
    }
}

// Encapsula la operacion admin ubicacion card usada por la pantalla o el estado.
@Composable
private fun AdminUbicacionCard(
    ubicacion: UbicacionResponse,
    onDeleteClick: () -> Unit
) {
    AdminBusinessCard {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ubicacion.nombre,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = if (ubicacion.activo) "Activa" else "Inactiva", color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar ubicacion", tint = errorColor)
        }
    }
}

// Encapsula la operacion admin business card usada por la pantalla o el estado.
@Composable
private fun AdminBusinessCard(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, secondaryColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

// Encapsula la operacion admin usuario manage dialog usada por la pantalla o el estado.
@Composable
private fun AdminUsuarioManageDialog(
    usuario: AdminUsuarioResponse,
    selectedRole: AdminUsuarioRole,
    isTrainer: Boolean,
    availableRoles: List<AdminUsuarioRole>,
    canEditTrainer: Boolean,
    canDelete: Boolean,
    onRoleChange: (AdminUsuarioRole) -> Unit,
    onTrainerChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val roleChanged = selectedRole != usuario.role()
    val trainerChanged = isTrainer != usuario.esEntrenador
    val trainerEnabled = canEditTrainer && selectedRole != AdminUsuarioRole.Cliente

    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = { Text("Gestionar usuario", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(usuario.correo, color = textColor)
                Text("Rol actual: ${usuario.roleLabel()}", color = Color.Gray)
                AdminUsuarioRoleSelector(
                    selectedRole = selectedRole,
                    availableRoles = availableRoles,
                    enabled = availableRoles.isNotEmpty(),
                    onRoleSelected = onRoleChange,
                    modifier = Modifier.fillMaxWidth()
                )
                AdminTrainerToggle(
                    checked = isTrainer,
                    enabled = trainerEnabled,
                    onCheckedChange = onTrainerChange
                )
                Button(
                    onClick = onSaveClick,
                    enabled = roleChanged || trainerChanged,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryColor,
                        disabledContainerColor = secondaryColor.copy(alpha = 0.35f),
                        contentColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }
                Button(
                    onClick = onDeleteClick,
                    enabled = canDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorColor,
                        disabledContainerColor = errorColor.copy(alpha = 0.35f),
                        contentColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar usuario")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin usuario create dialog usada por la pantalla o el estado.
@Composable
private fun AdminUsuarioCreateDialog(
    email: String,
    selectedRole: AdminUsuarioRole,
    isTrainer: Boolean,
    availableRoles: List<AdminUsuarioRole>,
    canEditTrainer: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onRoleChange: (AdminUsuarioRole) -> Unit,
    onTrainerChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val validEmail = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val trainerEnabled = canEditTrainer && selectedRole != AdminUsuarioRole.Cliente

    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Crear usuario", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("Correo electronico") },
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = adminUsuarioTextFieldColors(),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
                AdminUsuarioRoleSelector(
                    selectedRole = selectedRole,
                    availableRoles = availableRoles,
                    enabled = !isLoading && availableRoles.isNotEmpty(),
                    onRoleSelected = onRoleChange,
                    modifier = Modifier.fillMaxWidth()
                )
                AdminTrainerToggle(
                    checked = isTrainer,
                    enabled = trainerEnabled && !isLoading,
                    onCheckedChange = onTrainerChange
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        color = secondaryColor,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                if (errorMessage != null) {
                    Text(errorMessage, color = errorColor, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = validEmail && !isLoading && availableRoles.isNotEmpty()
            ) {
                Text("Crear", color = secondaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin trainer toggle usada por la pantalla o el estado.
@Composable
private fun AdminTrainerToggle(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = secondaryColor,
                uncheckedColor = secondaryColor,
                checkmarkColor = textColor,
                disabledCheckedColor = secondaryColor.copy(alpha = 0.35f),
                disabledUncheckedColor = Color.Gray
            )
        )
        Text("Asignar como entrenador", color = if (enabled) textColor else Color.Gray)
    }
}

// Encapsula la operacion admin usuario role selector usada por la pantalla o el estado.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminUsuarioRoleSelector(
    selectedRole: AdminUsuarioRole,
    availableRoles: List<AdminUsuarioRole>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onRoleSelected: (AdminUsuarioRole) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedRole.label,
            onValueChange = {},
            enabled = enabled,
            readOnly = true,
            singleLine = true,
            trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = textColor) },
            colors = adminUsuarioTextFieldColors(),
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
            availableRoles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.label, color = textColor) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Encapsula la operacion admin usuario role confirm dialog usada por la pantalla o el estado.
@Composable
private fun AdminUsuarioRoleConfirmDialog(
    roleChange: AdminUsuarioRoleChange,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = { Text("Cambiar usuario", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Quieres aplicar los cambios a ${roleChange.usuario.correo}?", color = textColor)
                Text("Nuevo estado: ${roleChange.previewLabel()}", color = Color.Gray)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirmar", color = secondaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin delete user dialog usada por la pantalla o el estado.
@Composable
private fun AdminDeleteUserDialog(
    usuario: AdminUsuarioResponse,
    confirmationEmail: String,
    onConfirmationEmailChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val deleteEnabled = confirmationEmail.trim().equals(usuario.correo, ignoreCase = true)
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = { Text("Eliminar usuario", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Esta accion no se puede deshacer. Escribe el correo para confirmar:", color = textColor)
                Text(usuario.correo, color = textColor, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = confirmationEmail,
                    onValueChange = onConfirmationEmailChange,
                    placeholder = { Text("Correo del usuario") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = adminUsuarioTextFieldColors(),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = deleteEnabled) { Text("Eliminar", color = errorColor) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin descuento edit dialog usada por la pantalla o el estado.
@Composable
private fun AdminDescuentoEditDialog(
    descuento: DescuentoResponse?,
    codigo: String,
    porcentaje: String,
    isLoading: Boolean,
    onCodigoChange: (String) -> Unit,
    onPorcentajeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val porcentajeValido = porcentaje.trim().replace(",", ".").toDoubleOrNull()
        ?.let { it > 0.0 && it <= 100.0 } == true
    val codigoValido = descuento == null || codigo.isNotBlank()
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = if (descuento == null) "Crear descuento" else "Editar descuento",
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = onCodigoChange,
                    placeholder = { Text(if (descuento == null) "Codigo opcional" else "Codigo") },
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = adminUsuarioTextFieldColors(),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = porcentaje,
                    onValueChange = onPorcentajeChange,
                    placeholder = { Text("Porcentaje") },
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = adminUsuarioTextFieldColors(),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = codigoValido && porcentajeValido && !isLoading) {
                Text(if (descuento == null) "Crear" else "Guardar", color = secondaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin ubicacion dialog usada por la pantalla o el estado.
@Composable
private fun AdminUbicacionDialog(
    nombre: String,
    isLoading: Boolean,
    onNombreChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Anadir ubicacion", color = textColor, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                placeholder = { Text("Nombre de la ubicacion") },
                enabled = !isLoading,
                singleLine = true,
                colors = adminUsuarioTextFieldColors(),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = nombre.trim().isNotEmpty() && !isLoading) {
                Text("Crear", color = secondaryColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin simple delete dialog usada por la pantalla o el estado.
@Composable
private fun AdminSimpleDeleteDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = { Text(title, color = textColor, fontWeight = FontWeight.Bold) },
        text = { Text(text, color = textColor) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Eliminar", color = errorColor) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = textColor) }
        }
    )
}

// Encapsula la operacion admin info dialog usada por la pantalla o el estado.
@Composable
private fun AdminInfoDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    isError: Boolean = false
) {
    AlertDialog(
        containerColor = backgroundColor,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = if (isError) errorColor else textColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(text, color = textColor) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Aceptar", color = secondaryColor) }
        }
    )
}

// Encapsula la operacion admin business loading usada por la pantalla o el estado.
@Composable
private fun AdminBusinessLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = secondaryColor)
    }
}

// Encapsula la operacion admin business message usada por la pantalla o el estado.
@Composable
private fun AdminBusinessMessage(message: String) {
    Text(
        text = message,
        color = textColor,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 18.dp)
    )
}

// Encapsula la operacion admin usuario text field colors usada por la pantalla o el estado.
@Composable
private fun adminUsuarioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    disabledTextColor = textColor.copy(alpha = 0.65f),
    cursorColor = primaryColor,
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = primaryColor,
    disabledBorderColor = primaryColor.copy(alpha = 0.55f),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedPlaceholderColor = textColor.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = textColor.copy(alpha = 0.55f)
)

// Encapsula la operacion list usada por la pantalla o el estado.
private fun List<AdminUsuarioResponse>.businessUserFilter(query: String): List<AdminUsuarioResponse> {
    val normalizedQuery = query.normalizedBusiness()
    if (normalizedQuery.isBlank()) return this
    return filter { usuario ->
        listOf(
            usuario.correo,
            usuario.telefono.orEmpty(),
            usuario.displayName(),
            usuario.roleLabel()
        ).any { it.normalizedBusiness().contains(normalizedQuery) }
    }
}

// Encapsula la operacion list usada por la pantalla o el estado.
private fun List<DescuentoResponse>.businessDiscountFilter(query: String): List<DescuentoResponse> {
    val normalizedQuery = query.normalizedBusiness()
    if (normalizedQuery.isBlank()) return this
    return filter { descuento ->
        descuento.codigo.normalizedBusiness().contains(normalizedQuery) ||
            descuento.porcentaje.toString().contains(normalizedQuery)
    }
}

// Encapsula la operacion list usada por la pantalla o el estado.
private fun List<UbicacionResponse>.businessLocationFilter(query: String): List<UbicacionResponse> {
    val normalizedQuery = query.normalizedBusiness()
    if (normalizedQuery.isBlank()) return this
    return filter { ubicacion -> ubicacion.nombre.normalizedBusiness().contains(normalizedQuery) }
}

// Encapsula la operacion string usada por la pantalla o el estado.
private fun String.normalizedBusiness(): String {
    return lowercase(Locale.getDefault()).trim()
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.role(): AdminUsuarioRole {
    return when {
        esSuperAdmin -> AdminUsuarioRole.SuperAdmin
        esAdmin -> AdminUsuarioRole.Admin
        else -> AdminUsuarioRole.Cliente
    }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.roleLabel(): String {
    return if (esEntrenador && role() != AdminUsuarioRole.Cliente) {
        "${role().label} - Entrenador"
    } else {
        role().label
    }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.displayName(): String {
    return listOfNotNull(
        nombre?.takeIf { it.isNotBlank() },
        apellido1?.takeIf { it.isNotBlank() }
    ).joinToString(" ")
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.canBeManagedBy(currentUser: UsuarioMeResponse?): Boolean {
    if (currentUser == null) return false
    if (currentUser.idUsuario == idUsuario) return false
    return when {
        currentUser.esSuperAdmin -> true
        currentUser.esAdmin -> !esAdmin && !esSuperAdmin
        else -> false
    }
}

// Encapsula la operacion admin usuario response usada por la pantalla o el estado.
private fun AdminUsuarioResponse.canBeDeletedBy(currentUser: UsuarioMeResponse?): Boolean {
    return currentUser?.esSuperAdmin == true && canBeManagedBy(currentUser)
}

// Encapsula la operacion usuario me response usada por la pantalla o el estado.
private fun UsuarioMeResponse?.availableRoles(): List<AdminUsuarioRole> {
    return when {
        this?.esSuperAdmin == true -> AdminUsuarioRole.values().toList()
        this?.esAdmin == true -> listOf(AdminUsuarioRole.Cliente, AdminUsuarioRole.Admin)
        else -> emptyList()
    }
}

// Agrupa la pantalla admin negocio tab y su estado visual principal.
private enum class AdminNegocioTab(val label: String) {
    Usuarios("Usuarios"),
    Descuentos("Descuentos"),
    Ubicaciones("Ubicaciones");

    companion object {
        // Encapsula la operacion from name usada por la pantalla o el estado.
        fun fromName(name: String): AdminNegocioTab {
            return values().firstOrNull { it.name == name } ?: Usuarios
        }
    }
}

// Agrupa la pantalla admin usuario role y su estado visual principal.
private enum class AdminUsuarioRole(val label: String) {
    Cliente("Cliente"),
    Admin("Admin"),
    SuperAdmin("Super-admin");

    companion object {
        // Encapsula la operacion from name usada por la pantalla o el estado.
        fun fromName(name: String): AdminUsuarioRole {
            return values().firstOrNull { it.name == name } ?: Cliente
        }
    }
}

// Agrupa la pantalla admin usuario role change y su estado visual principal.
private data class AdminUsuarioRoleChange(
    val usuario: AdminUsuarioResponse,
    val role: AdminUsuarioRole,
    val esEntrenador: Boolean
)

// Encapsula la operacion admin usuario role change usada por la pantalla o el estado.
private fun AdminUsuarioRoleChange.previewLabel(): String {
    val base = role.label
    return if (esEntrenador && role != AdminUsuarioRole.Cliente) "$base - Entrenador" else base
}



