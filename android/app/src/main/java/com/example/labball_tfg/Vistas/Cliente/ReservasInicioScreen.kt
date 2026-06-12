package com.example.labball_tfg.Vistas.Cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.labball_tfg.R
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor

// Renderiza la pantalla reservas inicio screen y conecta sus acciones principales.
@Composable
fun ReservasInicioScreen(
    onAddReservaClick: () -> Unit,
    onVerReservasClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.precios3),
            contentDescription = "Precios de bonos",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 420.dp)
        )

        Spacer(modifier = Modifier.height(34.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReservaInicioButton(
                text = "Añadir Reserva",
                onClick = onAddReservaClick,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(22.dp))

            ReservaInicioButton(
                text = "Ver Reservas",
                onClick = onVerReservasClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Encapsula la operacion reserva inicio button usada por la pantalla o el estado.
@Composable
private fun ReservaInicioButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = secondaryColor
        ),
        modifier = modifier.height(54.dp)
    ) {
        Text(
            text = text,
            color = textColor
        )
    }
}