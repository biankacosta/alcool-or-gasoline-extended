package com.example.alcoolougasolina

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme
import com.example.alcoolougasolina.ui.theme.LightGray
import com.example.alcoolougasolina.ui.theme.LightGreen
import com.example.alcoolougasolina.ui.theme.LightPink
import com.example.alcoolougasolina.ui.theme.TruePink
import com.example.alcoolougasolina.ui.theme.patuaFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")
val SWITCH_STATE_KEY = booleanPreferencesKey("switch_state")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlcoolOuGasolinaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Screen(
                        modifier = Modifier.padding(innerPadding),
                        context = this
                    )
                }
            }
        }
    }
}



fun calculateBestChoice(gasolina: String, alcool: String, percentage: Double): String {
    val gasolinaValor = gasolina.replace(",", ".").toDoubleOrNull()
    val alcoolValor = alcool.replace(",", ".").toDoubleOrNull()

    return if (gasolinaValor != null && alcoolValor != null) {
        if (alcoolValor / gasolinaValor <= percentage) {
            "Álcool é a melhor escolha!"
        } else {
            "Gasolina é a melhor escolha!"
        }
    } else {
        "Por favor, insira valores válidos."
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
        contentDescription = "Bomba de Gasolina",
        modifier = Modifier
            .size(70.dp)
            .padding(bottom = 8.dp),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun Screen(modifier: Modifier = Modifier, context: Context? = null)
    {
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

    var gasolinePrice by rememberSaveable { mutableStateOf("") }
    var alcoholPrice by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(true) }
    var resultado by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        context?.let {
            checked = getSwitchState(it)
        }
    }

    Column (
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.13f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            ThemedImage()
            Column(
                modifier = Modifier.padding(7.dp)
            ) {
                Text(
                    text = "ÁLCOOL",
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
                        )
                    )
                )

                Text(
                    text = "ou gasolina",
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
                        ),
                        fontSize = 24.sp
                    )
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(secondaryColor)
                .padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preço da Gasolina:",
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
                    Text(
                        text = "Digite o preço da gasolina",
                        color = Color(android.graphics.Color.parseColor("#696374"))
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGreen,
                    unfocusedContainerColor = LightGray,
                )
            )

            Text(
                text = "Preço do Álcool:",
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
                    Text(
                        text = "Digite o preço do álcool",
                        color = Color(android.graphics.Color.parseColor("#696374"))
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGreen,
                    unfocusedContainerColor = LightGray,
                )
            )


            Text(
                text = "O rendimento de álcool do seu carro é de 75%?",
                style = MaterialTheme.typography.labelSmall,
                color = tertiaryColor
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

            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val percentage = if (checked) 0.75 else 0.70
                        resultado = calculateBestChoice(gasolinePrice, alcoholPrice, percentage)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Calcular Melhor Opção")
                }

                if (resultado.isNotEmpty()) {
                    Text(
                        text = resultado,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = patuaFont,
                        color = TruePink,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    AlcoolOuGasolinaTheme {
        Screen()
    }
}