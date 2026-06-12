package com.example.labball_tfg.Vistas.Cliente

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labball_tfg.Modelo.JugadorResponse
import com.example.labball_tfg.ViewModel.CLIENTE.JugadorViewModel
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.primaryColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Renderiza la pantalla mi jugador screen y conecta sus acciones principales.
@Composable
fun MiJugadorScreen(
    token: String,
    viewModel: JugadorViewModel = viewModel()
) {
    val jugador by viewModel.jugador.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(token) {
        viewModel.cargarJugadorMe(token)
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        }

        errorMessage != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        jugador == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "La pantalla de jugador se desbloqueará una vez realices tu primera reserva. Esta pantalla está destinada al seguimiento del jugador.",
                    color = textColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        else -> {
            MiJugadorContent(jugador = jugador!!)
        }
    }
}

// Encapsula la operacion mi jugador content usada por la pantalla o el estado.
@Composable
private fun MiJugadorContent(
    jugador: JugadorResponse
) {
    val stats = listOf(
        PlayerStat("Tiro", jugador.tiro ?: 0),
        PlayerStat("Pase", jugador.pase ?: 0),
        PlayerStat("Físico", jugador.fisico ?: 0),
        PlayerStat("Defensa", jugador.defensa ?: 0),
        PlayerStat("Bote", jugador.bote ?: 0),
        PlayerStat("Velocidad", jugador.velocidad ?: 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MiJugadorHeader(jugador)

        HorizontalDivider(
            color = textColor,
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RadarChart(
                stats = stats,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    color = textColor,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                StatsGrid(stats = stats)
            }
        }
    }
}

// Encapsula la operacion mi jugador header usada por la pantalla o el estado.
@Composable
private fun MiJugadorHeader(jugador: JugadorResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp, top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${jugador.nombre} ${jugador.apellidos}",
            color = textColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            MiJugadorHeaderMetadata(jugador.posicion ?: "Sin posición")
            MiJugadorHeaderMetadata(jugador.altura.toPlayerHeightLabel())
            MiJugadorHeaderMetadata(jugador.peso.toPlayerWeightLabel())
        }
    }
}

// Encapsula la operacion mi jugador header metadata usada por la pantalla o el estado.
@Composable
private fun MiJugadorHeaderMetadata(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 16.sp
    )
}

// Encapsula la operacion radar chart usada por la pantalla o el estado.
@Composable
private fun RadarChart(
    stats: List<PlayerStat>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.36f

        val gridColor = textColor.copy(alpha = 0.75f)
        val fillColor = secondaryColor.copy(alpha = 0.92f)

        // Encapsula la operacion point usada por la pantalla o el estado.
        fun point(index: Int, percent: Float): Offset {
            val angle = (-90f + index * 60f) * (PI.toFloat() / 180f)
            return Offset(
                x = center.x + cos(angle) * radius * percent,
                y = center.y + sin(angle) * radius * percent
            )
        }

        val outerPoints = stats.indices.map { point(it, 1f) }
        val innerPoints = stats.indices.map { point(it, 0.7f) }

        drawPolygon(
            points = outerPoints,
            color = gridColor,
            strokeWidth = 5f
        )

        drawPolygon(
            points = innerPoints,
            color = gridColor.copy(alpha = 0.55f),
            strokeWidth = 3f
        )

        stats.indices.forEach { index ->
            drawLine(
                color = gridColor.copy(alpha = 0.35f),
                start = center,
                end = outerPoints[index],
                strokeWidth = 2f
            )
        }

        val statPoints = stats.mapIndexed { index, stat ->
            point(index, stat.value.coerceIn(0, 100) / 100f)
        }

        val statPath = Path().apply {
            statPoints.forEachIndexed { index, offset ->
                if (index == 0) {
                    moveTo(offset.x, offset.y)
                } else {
                    lineTo(offset.x, offset.y)
                }
            }
            close()
        }

        drawPath(
            path = statPath,
            color = fillColor
        )

        val labelPaint = Paint().apply {
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = 34f
            isFakeBoldText = true
        }

        val valuePaint = Paint().apply {
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = 36f
            isFakeBoldText = true
        }

        stats.forEachIndexed { index, stat ->
            val labelPoint = point(index, 1.2f)

            drawContext.canvas.nativeCanvas.drawText(
                "${stat.name}:",
                labelPoint.x,
                labelPoint.y,
                labelPaint
            )

            drawContext.canvas.nativeCanvas.drawText(
                stat.value.coerceIn(0, 100).toString(),
                labelPoint.x,
                labelPoint.y + 34f,
                valuePaint
            )
        }
    }
}

// Encapsula la operacion draw scope usada por la pantalla o el estado.
private fun DrawScope.drawPolygon(
    points: List<Offset>,
    color: Color,
    strokeWidth: Float
) {
    points.forEachIndexed { index, point ->
        val nextPoint = points[(index + 1) % points.size]
        drawLine(
            color = color,
            start = point,
            end = nextPoint,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

// Encapsula la operacion stats grid usada por la pantalla o el estado.
@Composable
private fun StatsGrid(
    stats: List<PlayerStat>
) {
    val rows = listOf(
        stats[0] to stats[1],
        stats[2] to stats[3],
        stats[4] to stats[5]
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                StatBar(
                    stat = row.first,
                    modifier = Modifier.weight(1f)
                )

                StatBar(
                    stat = row.second,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Encapsula la operacion stat bar usada por la pantalla o el estado.
@Composable
private fun StatBar(
    stat: PlayerStat,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stat.name,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = {
                stat.value.coerceIn(0, 100) / 100f
            },
            color = secondaryColor,
            trackColor = primaryColor.copy(alpha = 0.35f),
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
    }
}

// Agrupa la pantalla player stat y su estado visual principal.
private data class PlayerStat(
    val name: String,
    val value: Int
)

// Encapsula la operacion double usada por la pantalla o el estado.
private fun Double?.toPlayerHeightLabel(): String {
    return this?.let {
        "${String.format(Locale("es", "ES"), "%.2f", it)} m"
    } ?: "- m"
}

// Encapsula la operacion double usada por la pantalla o el estado.
private fun Double?.toPlayerWeightLabel(): String {
    return this?.let {
        "${String.format(Locale("es", "ES"), "%.2f", it)} kg"
    } ?: "- kg"
}
