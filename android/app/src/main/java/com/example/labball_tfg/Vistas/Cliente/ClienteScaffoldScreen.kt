package com.example.labball_tfg.Vistas.Cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labball_tfg.R
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.labball_tfg.Vistas.PerfilScreen
import com.example.labball_tfg.Vistas.Cliente.Reservas.RealizarReservaScreen
import com.example.labball_tfg.Vistas.Cliente.Reservas.VerReservasScreen

// Renderiza la pantalla cliente scaffold screen y conecta sus acciones principales.
@Composable
fun ClienteScaffoldScreen(
    token: String,
    onAddSessionClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(2) }
    var reservaSubScreen by remember { mutableStateOf("inicio") }


    val selectedTitle = when (selectedTab) {
        0 -> "Reservar sesiones"
        1 -> "Mi Jugador"
        2 -> "Mis sesiones"
        3 -> "Entrena por tu cuenta"
        4 -> "Perfil"
        else -> ""
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            ClienteTopBar()
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = textColor,
                    thickness = 1.dp
                )
                NavigationBar(
                    containerColor = backgroundColor,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            reservaSubScreen = "inicio"
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Reservar sesiones",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.SportsBasketball,
                                contentDescription = "Mi jugador",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Mis sesiones",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.OndemandVideo,
                                contentDescription = "Entrena por tu cuenta",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Perfil",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            ClienteSectionTitle(title = selectedTitle)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(backgroundColor)
            ) {
                when (selectedTab) {
                    0 -> {
                        when (reservaSubScreen) {
                            "inicio" -> ReservasInicioScreen(
                                onAddReservaClick = { reservaSubScreen = "crear" },
                                onVerReservasClick = { reservaSubScreen = "ver" }
                            )

                            "crear" -> RealizarReservaScreen(
                                token = token,
                                onBackToReservasInicio = {
                                    reservaSubScreen = "inicio"
                                }
                            )

                            "ver" -> VerReservasScreen(
                                token = token,
                                onBackToReservasInicio = {
                                    reservaSubScreen = "inicio"
                                }
                            )
                        }
                    }

                    1 -> MiJugadorScreen(token)
                    2 -> MisSesionesScreen(
                        token = token,
                        onAddSessionClick = {
                            selectedTab = 0
                            reservaSubScreen = "inicio"
                        }
                    )

                    3 -> EntrenaPorTuCuentaScreen(token)
                    4 -> PerfilScreen(
                        token = token,
                        onChangePasswordClick = onChangePasswordClick,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        }
    }
}

// Encapsula la operacion cliente top bar usada por la pantalla o el estado.
@Composable
private fun ClienteTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Labball",
                modifier = Modifier.size(54.dp)
            )

            Text(
                text = "Labball",
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 24.sp
            )
        }

        HorizontalDivider(
            color = textColor,
            thickness = 1.dp
        )
    }
}

// Encapsula la operacion cliente section title usada por la pantalla o el estado.
@Composable
private fun ClienteSectionTitle(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(top = 16.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 28.dp, bottom = 10.dp)
        )

        HorizontalDivider(
            color = textColor,
            thickness = 1.dp
        )
    }
}

// Encapsula la operacion cliente navigation colors usada por la pantalla o el estado.
@Composable
private fun clienteNavigationColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = secondaryColor,
    unselectedIconColor = textColor,
    indicatorColor = Color.Transparent
)

// Muestra una vista previa de cliente scaffold screen preview para Compose.
@Composable
@Preview(showBackground = true)
fun ClienteScaffoldScreenPreview() {
    ClienteScaffoldScreen("", {}, {}, {})
}
