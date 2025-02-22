package com.example.alcoolougasolina

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List() {
    val context = LocalContext.current
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().align(Alignment.Center),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.14f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                ThemedImage()
                Text(
                    text = stringResource(id = R.string.list_name),
                    modifier = Modifier.padding(7.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
                        )
                    )
                )

            }
            val stations = getGasStations(context)

            Column (
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .padding(7.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(secondaryColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nome do posto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = tertiaryColor
                )
                Text(
                    text = "Localização: coordenadas aqui",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }

            // Mostrando no Log os postos para debug
            Log.d("GasStations", "Lista de postos: $stations")

            // Exibindo os postos em uma LazyColumn (ou Column, caso prefira)
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stations) { station ->
                    // Aqui, você pode exibir informações de cada posto, como nome e preço
                    Text(
                        text = "Nome do posto: ${station.stationName}, Localização: ${station.latitude}, ${station.longitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

