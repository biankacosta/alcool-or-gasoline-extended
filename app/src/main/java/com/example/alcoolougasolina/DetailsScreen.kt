package com.example.alcoolougasolina

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavHostController, stationId: String) {
    val context = LocalContext.current
    val station = getGasStationById(context, stationId)
    var showMap by remember { mutableStateOf(false) }

    if (station != null) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.details)) }) // Agora usa a string traduzida
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "${stringResource(R.string.station_name)}: ${station.stationName ?: stringResource(R.string.unnamed_gas_station)}")
                Text(text = "${stringResource(R.string.gasoline_price)}: R$ ${station.gasolinePrice}")
                Text(text = "${stringResource(R.string.alcohol_price)}: R$ ${station.alcoholPrice}")
                Text(text = "${stringResource(R.string.registration_date)}: ${station.registrationDate}")

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para abrir/fechar o mapa
                Button(onClick = { showMap = !showMap }) {
                    Text(text = stringResource(if (showMap) R.string.close_map else R.string.open_map))
                }

                // Exibe o mapa condicionalmente
                if (showMap && !station.latitude.isNullOrEmpty() && !station.longitude.isNullOrEmpty()) {
                    val location = LatLng(station.latitude.toDouble(), station.longitude.toDouble())
                    Spacer(modifier = Modifier.height(16.dp))
                    MapViewComponent(location)
                } else if (!showMap) {
                    Text(text = stringResource(R.string.map_closed))
                } else {
                    Text(text = stringResource(R.string.location_not_available))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão Editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        Log.d("DetailsScreen", "stationId: $stationId")
                        navController.navigate("edit/$stationId")
                    }) {
                        Text(text = stringResource(R.string.edit)) // Usa a string traduzida
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botão Voltar
                    Button(onClick = { navController.navigateUp() }) {
                        Text(text = stringResource(R.string.back)) // Usa a string traduzida
                    }
                }
            }
        }
    } else {
        Text(stringResource(R.string.station_not_found)) // Agora traduzível
    }
}


@SuppressLint("MissingPermission")
@Composable
fun MapViewComponent(location: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = location),
            title = "Local do Posto"
        )
    }
}