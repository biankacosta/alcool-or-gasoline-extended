import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alcoolougasolina.R
import com.example.alcoolougasolina.getGasStationById
import com.example.alcoolougasolina.updateGasStation
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavHostController, stationId: String) {
    Log.d("EditScreen", "stationId recebido: $stationId")
    val context = LocalContext.current
    val station by remember { mutableStateOf(getGasStationById(context, stationId)) }
    var stationName by remember { mutableStateOf(station?.stationName?: "") }
    var gasolinePrice by remember { mutableStateOf(station?.gasolinePrice?.toString()?: "") }
    var alcoholPrice by remember { mutableStateOf(station?.alcoholPrice?.toString()?: "") }

    // Obter o idioma do sistema
    val language = Locale.getDefault().language

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = stationName,
            onValueChange = { stationName = it },
            label = { Text(text = stringResource(R.string.station_name)) } // Traduzir o label
        )
        OutlinedTextField(
            value = gasolinePrice,
            onValueChange = { gasolinePrice = it },
            label = { Text(text = stringResource(R.string.gasoline_price)) } // Traduzir o label
        )
        OutlinedTextField(
            value = alcoholPrice,
            onValueChange = { alcoholPrice = it },
            label = { Text(text = stringResource(R.string.alcohol_price)) } // Traduzir o label
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val updatedStation = station?.copy(
                stationName = stationName,
                gasolinePrice = gasolinePrice.toFloatOrNull()?: 0.0f,
                alcoholPrice = alcoholPrice.toFloatOrNull()?: 0.0f
            )
            if (updatedStation!= null) {
                updateGasStation(
                    context,
                    updatedStation.id,
                    updatedStation.stationName,
                    updatedStation.gasolinePrice.toFloat(),
                    updatedStation.alcoholPrice.toFloat(),
                    updatedStation.consumption
                )
                navController.navigate("details/$stationId") {
                    popUpTo("details/$stationId") { inclusive = true }
                }
            }
        }) {
            // Traduzir o botão "Salvar"
            Text(text = stringResource(if (language == "en") R.string.salvar else R.string.salvar))
        }
    }
}