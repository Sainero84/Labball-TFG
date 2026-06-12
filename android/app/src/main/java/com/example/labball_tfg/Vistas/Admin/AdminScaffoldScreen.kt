package com.example.labball_tfg.Vistas.Admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PermContactCalendar
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labball_tfg.R
import com.example.labball_tfg.Vistas.PerfilScreen
import com.example.labball_tfg.ui.theme.backgroundColor
import com.example.labball_tfg.ui.theme.secondaryColor
import com.example.labball_tfg.ui.theme.textColor

// Renderiza la pantalla admin scaffold screen y conecta sus acciones principales.
@Composable
fun AdminScaffoldScreen(
    token: String,
    onChangePasswordClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(2) }


    val selectedTitle = when (selectedTab) {
        0 -> "Gestionar Negocio"
        1 -> "Jugadores"
        2 -> "Gestionar Sesiones"
        3 -> "Entrena por tu cuenta"
        4 -> "Perfil"
        else -> ""
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AdminTopBar()
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
                        selectedTab == 0,
                        { selectedTab = 0 },
                        {
                            Icon(
                                Icons.Filled.PermContactCalendar,
                                "Gestionar Negocio",
                                Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selectedTab == 1,
                        { selectedTab = 1 },
                        {
                            Icon(
                                Icons.Filled.SportsBasketball,
                                "Mi jugador",
                                Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selectedTab == 2,
                        { selectedTab = 2 },
                        {
                            Icon(
                                Icons.Filled.Home,
                                "Mis sesiones",
                                Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selectedTab == 3,
                        { selectedTab = 3 },
                        {
                            Icon(
                                Icons.Filled.OndemandVideo,
                                "Entrena por tu cuenta",
                                Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )

                    NavigationBarItem(
                        selectedTab == 4,
                        { selectedTab = 4 },
                        {
                            Icon(
                                Icons.Filled.Person,
                                "Perfil",
                                Modifier.size(32.dp)
                            )
                        },
                        colors = clienteNavigationColors()
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            AdminSectionTitle(title = selectedTitle)

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(backgroundColor)
            ) {
                when (selectedTab) {
                    0 -> AdminUsuariosScreen(token)
                    1 -> AdminJugadoresScreen(token)
                    2 -> AdminReservasScreen(token)
                    3 -> AdminEntrenaPorTuCuentaScreen(token)
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



// Encapsula la operacion admin top bar usada por la pantalla o el estado.
@Composable
private fun AdminTopBar() {
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

// Encapsula la operacion admin section title usada por la pantalla o el estado.
@Composable
private fun AdminSectionTitle(title: String) {
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

// Muestra una vista previa de admin scaffold screen preview para Compose.
@Composable
@Preview(showBackground = true)
fun AdminScaffoldScreenPreview() {
    AdminScaffoldScreen("")
}


