package com.example.labball_tfg.Vistas.Admin

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.ViewModel.ADMIN.AdminMediaViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor

@Composable
fun AdminPublicarVideoScreen(
    token: String,
    onBack: () -> Unit,
    onVideoPublicado: () -> Unit,
    viewModel: AdminMediaViewModel = viewModel()
) {
    val context = LocalContext.current
    val mediaGuardado by viewModel.mediaGuardado.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoNombre by remember { mutableStateOf<String?>(null) }
    var videoMimeType by remember { mutableStateOf("video/*") }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            videoUri = uri
            videoNombre = adminVideoFileName(context, uri)
            videoMimeType = context.contentResolver.getType(uri) ?: "video/*"
            viewModel.limpiarEstado()
        }
    }

    LaunchedEffect(mediaGuardado) {
        if (mediaGuardado != null) {
            viewModel.limpiarEstado()
            onVideoPublicado()
        }
    }

    BackHandler(enabled = !isLoading) { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.35f)
                .border(2.dp, secondaryColor, RoundedCornerShape(12.dp))
                .clickable(enabled = !isLoading) {
                    videoPicker.launch("video/*")
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Upload,
                    contentDescription = "Subir video",
                    tint = secondaryColor,
                    modifier = Modifier.size(58.dp)
                )

                Text(
                    text = videoNombre ?: "Subir video",
                    color = secondaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "Titulo:",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        AdminVideoTextField(
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Ej: 1vs1 en medio campo",
            singleLine = true,
            modifier = Modifier.height(58.dp)
        )

        Text(
            text = "Descripcion:",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        AdminVideoTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Escriba una descripcion:",
            singleLine = false,
            modifier = Modifier.weight(0.65f)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage.orEmpty(),
                color = Color(0xFFFF8A80),
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                videoUri?.let { uri ->
                    viewModel.subirVideoDesdeUri(
                        token = token,
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        videoUri = uri,
                        mimeType = videoMimeType,
                        context = context.applicationContext
                    )
                }
            },
            enabled = titulo.isNotBlank() && videoUri != null && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                disabledContainerColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(42.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = textColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Publicar Video",
                    color = textColor,
                    fontSize = 13.sp
                )
            }
        }
    }
}

private fun adminVideoFileName(
    context: Context,
    uri: Uri
): String {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )

    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }

    return "Video seleccionado"
}

@Composable
private fun AdminVideoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = primaryColor,
            focusedBorderColor = secondaryColor,
            unfocusedBorderColor = secondaryColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )
}
