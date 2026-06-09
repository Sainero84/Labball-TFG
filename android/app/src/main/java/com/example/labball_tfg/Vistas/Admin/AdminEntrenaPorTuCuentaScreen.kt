package com.example.labball_tfg.Vistas.Admin

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.OptIn
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.labball_tfg.Modelo.MediaUpdateRequest
import com.example.labball_tfg.Modelo.VideoDetailResponse
import com.example.labball_tfg.Modelo.VideoListItemResponse
import com.example.labball_tfg.ViewModel.ADMIN.AdminMediaViewModel
import com.example.labball_tfg.ViewModel.CLIENTE.MediaViewModel
import com.example.labball_tfg.ViewModel.UsuarioViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.errorColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.labball_tfg.ui.theme.primaryColor

@Composable
fun AdminEntrenaPorTuCuentaScreen(
    token: String,
    viewModel: MediaViewModel = viewModel(),
    adminMediaViewModel: AdminMediaViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel(),
    onPublicarVideoClick: () -> Unit = {}
) {
    val videos by viewModel.videos.collectAsState()
    val videoSeleccionado by viewModel.videoSeleccionado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val usuarioActual by usuarioViewModel.usuario.collectAsState()
    val mediaGuardado by adminMediaViewModel.mediaGuardado.collectAsState()
    val mediaEliminado by adminMediaViewModel.mediaEliminado.collectAsState()
    val adminActionLoading by adminMediaViewModel.isLoading.collectAsState()
    val adminActionError by adminMediaViewModel.errorMessage.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var mostrandoPublicarVideo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(token) {
        viewModel.cargarVideos(token)
        usuarioViewModel.cargarUsuarioMe(token)
    }

    LaunchedEffect(mediaGuardado?.idMedia, mostrandoPublicarVideo) {
        val media = mediaGuardado
        if (media != null && !mostrandoPublicarVideo) {
            viewModel.cargarVideos(token)
            viewModel.cargarVideoPorId(token, media.idMedia)
            adminMediaViewModel.limpiarEstado()
        }
    }

    LaunchedEffect(mediaEliminado) {
        if (mediaEliminado) {
            viewModel.limpiarVideoSeleccionado()
            viewModel.cargarVideos(token)
            adminMediaViewModel.limpiarEstado()
        }
    }

    val videosFiltrados = videos.filter {
        it.titulo.contains(busqueda, ignoreCase = true)
    }

    if (mostrandoPublicarVideo) {
        AdminPublicarVideoScreen(
            token = token,
            onBack = {
                mostrandoPublicarVideo = false
            },
            onVideoPublicado = {
                mostrandoPublicarVideo = false
                viewModel.cargarVideos(token)
            },
            viewModel = adminMediaViewModel
        )
        return
    }

    if (videoSeleccionado != null) {
        VideoFullScreen(
            video = videoSeleccionado!!,
            canDelete = usuarioActual?.esSuperAdmin == true,
            isActionLoading = adminActionLoading,
            actionError = adminActionError,
            onBack = {
                adminMediaViewModel.limpiarEstado()
                viewModel.limpiarVideoSeleccionado()
            },
            onUpdateVideo = { idMedia, titulo, descripcion ->
                adminMediaViewModel.actualizarVideo(
                    token = token,
                    idMedia = idMedia,
                    media = MediaUpdateRequest(
                        titulo = titulo,
                        descripcion = descripcion
                    )
                )
            },
            onDeleteVideo = { idMedia ->
                adminMediaViewModel.eliminarVideo(
                    token = token,
                    idMedia = idMedia
                )
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .padding(bottom = 62.dp)
        ) {

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = primaryColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = primaryColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedPlaceholderColor = textColor,
                    unfocusedPlaceholderColor = textColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = secondaryColor)
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "",
                        color = textColor
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(videosFiltrados) { video ->
                            VideoListItem(
                                video = video,
                                onClick = {
                                    viewModel.cargarVideoPorId(
                                        token = token,
                                        idMedia = video.idMedia
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                mostrandoPublicarVideo = true
                onPublicarVideoClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryColor
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Publicar Video",
                color = textColor
            )
        }
    }
}

@Composable
private fun VideoListItem(
    video: VideoListItemResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = video.urlMiniatura,
            contentDescription = video.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 110.dp, height = 78.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = video.titulo,
                color = textColor,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = video.descripcion ?: "",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoFullScreen(
    video: VideoDetailResponse,
    canDelete: Boolean,
    isActionLoading: Boolean,
    actionError: String?,
    onBack: () -> Unit,
    onUpdateVideo: (idMedia: Int, titulo: String, descripcion: String?) -> Unit,
    onDeleteVideo: (idMedia: Int) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val activity = remember(context) { context.findActivity() }
    var isVideoFullscreen by rememberSaveable(video.urlArchivo) { mutableStateOf(false) }
    var isEditing by rememberSaveable(video.idMedia) { mutableStateOf(false) }
    var editTitle by rememberSaveable(video.idMedia) { mutableStateOf(video.titulo) }
    var editDescription by rememberSaveable(video.idMedia) { mutableStateOf(video.descripcion.orEmpty()) }
    var showDeleteConfirmation by rememberSaveable(video.idMedia) { mutableStateOf(false) }

    LaunchedEffect(video.idMedia, video.titulo, video.descripcion) {
        if (!isEditing) {
            editTitle = video.titulo
            editDescription = video.descripcion.orEmpty()
        }
    }

    val exoPlayer = remember(video.urlArchivo) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.urlArchivo))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    DisposableEffect(isVideoFullscreen, activity) {
        if (isVideoFullscreen && activity != null) {
            WindowCompat.getInsetsController(activity.window, view).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        onDispose {
            if (isVideoFullscreen && activity != null) {
                WindowCompat.getInsetsController(activity.window, view)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack, enabled = !isActionLoading) {
                Text(
                    text = "Volver",
                    color = secondaryColor
                )
            }

            TextButton(
                onClick = {
                    isEditing = !isEditing
                    editTitle = video.titulo
                    editDescription = video.descripcion.orEmpty()
                },
                enabled = !isActionLoading
            ) {
                Text(
                    text = if (isEditing) "Cancelar" else "Editar",
                    color = secondaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editTitle,
                onValueChange = { editTitle = it },
                label = { Text("Titulo") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = adminVideoDetailTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = editDescription,
                onValueChange = { editDescription = it },
                label = { Text("Descripcion") },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = adminVideoDetailTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onUpdateVideo(
                        video.idMedia,
                        editTitle.trim(),
                        editDescription.trim().takeIf { it.isNotBlank() }
                    )
                    isEditing = false
                },
                enabled = editTitle.isNotBlank() && !isActionLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = secondaryColor,
                    disabledContainerColor = secondaryColor.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (isActionLoading) {
                    CircularProgressIndicator(
                        color = textColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Guardar cambios", color = textColor)
                }
            }
        } else {
            Text(
                text = video.titulo,
                color = textColor,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    player = exoPlayer
                    useController = true
                    setFullscreenButtonClickListener { fullscreen ->
                        isVideoFullscreen = fullscreen
                    }
                    setFullscreenButtonState(false)
                }
            },
            update = { playerView ->
                playerView.player = if (isVideoFullscreen) null else exoPlayer
                playerView.setFullscreenButtonState(isVideoFullscreen)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        if (isVideoFullscreen) {
            Dialog(
                onDismissRequest = { isVideoFullscreen = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                AndroidView(
                    factory = { viewContext ->
                        PlayerView(viewContext).apply {
                            player = exoPlayer
                            useController = true
                            setFullscreenButtonClickListener { fullscreen ->
                                isVideoFullscreen = fullscreen
                            }
                            setFullscreenButtonState(true)
                        }
                    },
                    update = { playerView ->
                        playerView.player = exoPlayer
                        playerView.setFullscreenButtonState(true)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isEditing) {
            Text(
                text = video.descripcion ?: "",
                color = textColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 30.sp
                )
            )
        }

        if (actionError != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = actionError,
                color = errorColor,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (canDelete) {
            TextButton(
                onClick = { showDeleteConfirmation = true },
                enabled = !isActionLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Eliminar video",
                    color = errorColor,
                    fontSize = 12.sp
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            containerColor = backgroundColor,
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Eliminar video",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Seguro que quieres eliminar este video?",
                    color = textColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteVideo(video.idMedia)
                    },
                    enabled = !isActionLoading
                ) {
                    Text("Eliminar", color = errorColor)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isActionLoading
                ) {
                    Text("Cancelar", color = textColor)
                }
            }
        )
    }
}

@Composable
private fun adminVideoDetailTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = primaryColor,
    focusedBorderColor = secondaryColor,
    unfocusedBorderColor = secondaryColor,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedLabelColor = textColor,
    unfocusedLabelColor = textColor.copy(alpha = 0.65f)
)

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
