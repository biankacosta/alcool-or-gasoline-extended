package com.example.alcoolougasolina

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.alcoolougasolina.ui.theme.LightGray
import com.example.alcoolougasolina.ui.theme.LightGreen
import com.example.alcoolougasolina.ui.theme.TruePink
import com.example.alcoolougasolina.ui.theme.patuaFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")
val SWITCH_STATE_KEY = booleanPreferencesKey("switch_state")

@Composable
fun Calcular() {
    Screen(context = LocalContext.current)
}


fun calculateBestChoice(gasolina: String, alcool: String, percentage: Double): Int {
    val gasolinaValor = gasolina.replace(",", ".").toDoubleOrNull()
    val alcoolValor = alcool.replace(",", ".").toDoubleOrNull()

    return if (gasolinaValor != null && alcoolValor != null) {
        if (alcoolValor / gasolinaValor <= percentage) {
            R.string.best_choice_alcohol
        } else {
            R.string.best_choice_gasoline
        }
    } else {
        R.string.insert_valid_values
    }
}

suspend fun saveSwitchState(context: Context, isChecked: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[SWITCH_STATE_KEY] = isChecked
    }
}

suspend fun getSwitchState(context: Context): Boolean {
    return context.dataStore.data
        .map { preferences -> preferences[SWITCH_STATE_KEY] ?: false }
        .first()
}

@Composable
fun ThemedImage() {
    val isDarkTheme = isSystemInDarkTheme()
    val imageRes = if (isDarkTheme) {
        R.drawable.bomba_de_combustivel_light // Imagem para tema escuro
    } else {
        R.drawable.bomba_de_combustivel_dark // Imagem para tema claro
    }

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = stringResource(id = R.string.gas_pump_description),
        modifier = Modifier
            .size(50.dp)
            .padding(end = 5.dp),
        contentScale = ContentScale.Fit
    )
}


@Composable
fun Screen(modifier: Modifier = Modifier, context: Context? = null)
    {
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

    var gasStationName by rememberSaveable { mutableStateOf("") }
    var gasolinePrice by rememberSaveable { mutableStateOf("") }
    var alcoholPrice by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(true) }
    var resultado by rememberSaveable { mutableStateOf(0) }

        

    LaunchedEffect(Unit) {
        context?.let {
            checked = getSwitchState(it)
        }
    }

    Column (
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemedImage()
            Text(
                text = stringResource(id = R.string.calculate_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
                    )
                )
            )

        }
        Column(
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
                text = stringResource(id = R.string.gas_station_name),
                style = MaterialTheme.typography.bodyLarge,
                color = tertiaryColor
            )

            TextField(
                value = gasStationName,
                onValueChange = { newText -> gasStationName = newText },
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, tertiaryColor, RoundedCornerShape(8.dp)),
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primaryContainer),
                placeholder = {
                    ProvideTextStyle(value = TextStyle(fontSize = 17.sp)) {
                        Text(
                            text = stringResource(id = R.string.enter_station_name),
                            color = Color(android.graphics.Color.parseColor("#696374"))
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGreen,
                    unfocusedContainerColor = LightGray,
                )
            )

            Text(
                text = stringResource(id = R.string.gasoline_price),
                style = MaterialTheme.typography.bodyLarge,
                color = tertiaryColor
            )

            TextField(
                value = gasolinePrice,
                onValueChange = { newText -> gasolinePrice = newText },
                modifier = Modifier.fillMaxWidth().border(1.dp, tertiaryColor, RoundedCornerShape(8.dp)),
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primaryContainer),
                placeholder = {
                    ProvideTextStyle(value = TextStyle(fontSize = 17.sp)){
                        Text(
                            text = stringResource(id = R.string.enter_gasoline_price),
                            color = Color(android.graphics.Color.parseColor("#696374"))
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGreen,
                    unfocusedContainerColor = LightGray,
                )
            )

            Text(
                text = stringResource(id = R.string.alcohol_price),
                style = MaterialTheme.typography.bodyLarge,
                color = tertiaryColor
            )

            TextField(
                value = alcoholPrice,
                onValueChange = { newText -> alcoholPrice = newText },
                modifier = Modifier.fillMaxWidth().border(1.dp, tertiaryColor, RoundedCornerShape(8.dp)),
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primaryContainer),
                placeholder = {
                    ProvideTextStyle(value = TextStyle(fontSize = 17.sp)) {
                        Text(
                            text = stringResource(id = R.string.enter_alcohol_price),
                            color = Color(android.graphics.Color.parseColor("#696374"))
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGreen,
                    unfocusedContainerColor = LightGray,
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.performance_question),
                    style = MaterialTheme.typography.labelSmall,
                    color = tertiaryColor,
                    modifier = Modifier.width(180.dp),
                    softWrap = true
                )
                Switch(
                    checked = checked,
                    onCheckedChange = { newChecked ->
                        checked = newChecked
                        CoroutineScope(Dispatchers.IO).launch {
                            if (context != null) {
                                saveSwitchState(context, newChecked)
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TruePink,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = Color.Gray,
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Button(
                    onClick = {
                        val percentage = if (checked) 0.75 else 0.70
                        resultado = calculateBestChoice(gasolinePrice, alcoholPrice, percentage) // Agora retorna um ID de recurso (Int)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.calculate_button))
                }

                Button(
                    onClick = {
                        if (context != null) {
                            addNewGasStation(context, gasStationName, gasolinePrice, alcoholPrice, checked)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(text = stringResource(id = R.string.save_station_button))
                }

                Spacer(modifier = Modifier.width(16.dp))
                if (resultado != 0) {
                    Text(
                        text = stringResource(id = resultado),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = patuaFont,
                        color = TruePink,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }
    }
}