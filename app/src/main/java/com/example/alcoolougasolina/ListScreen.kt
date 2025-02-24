package com.example.alcoolougasolina

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ListScreen(navController: NavHostController) {
    val context = LocalContext.current
    var stations by remember { mutableStateOf(getGasStations(context)) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.14f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                ThemedImage()
                Text(
                    text = stringResource(id = R.string.list_name), // Lista de postos
                    modifier = Modifier.padding(7.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (stations.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_stations), // Nenhum posto cadastrado ainda.
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(stations) { station ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${stringResource(R.string.station_name)}: ${station.stationName?.takeIf { it.isNotEmpty() } ?: stringResource(R.string.unnamed_gas_station)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${stringResource(R.string.gasoline_price)}: R$ ${station.gasolinePrice}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${stringResource(R.string.alcohol_price)}: R$ ${station.alcoholPrice}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${stringResource(R.string.performance_question)} ${if (station.consumption) stringResource(R.string.yes) else stringResource(R.string.no)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(7.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(onClick = { navController.navigate("details/${station.id}") }) {
                                        Text(stringResource(R.string.details)) // Detalhes
                                    }
                                    Button(
                                        onClick = {
                                            stations = stations.filter { it.id != station.id }
                                            deleteGasStation(context, station.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text(stringResource(R.string.excluir)) // Excluir
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListScreen() {
    val navController = rememberNavController()
    ListScreen(navController)
}
