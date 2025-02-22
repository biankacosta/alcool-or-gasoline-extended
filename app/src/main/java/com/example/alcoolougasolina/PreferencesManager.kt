package com.example.alcoolougasolina

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

//Função para gerar IDs únicos
fun generateUniqueId(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("gasStations", Context.MODE_PRIVATE)
    val gasStationsJson = sharedPreferences.getString("gasStationsList", "[]")
    val gasStationsList = gson.fromJson(gasStationsJson, Array<GasStationInfo>::class.java).toList()

    return if (gasStationsList.isEmpty()) {
        1
    } else {
        gasStationsList.maxOf { it.id } + 1 // Incrementa o maior ID encontrado
    }
}

// Função para salvar um posto de gasolina
fun addNewGasStation(context: Context, stationName: String?, gasolinePrice: String, alcoholPrice: String, consumption: Boolean) {
    // Gera um ID único para o novo posto
    val id = generateUniqueId(context)

    // Cria um novo objeto GasStationInfo com as informações fornecidas
    val newStation = GasStationInfo(id, stationName, gasolinePrice, alcoholPrice, consumption)

    // Pega os postos existentes, adiciona o novo posto e os salva novamente
    val sharedPreferences = context.getSharedPreferences("GasStationPrefs", Context.MODE_PRIVATE)
    val gasStationsJson = sharedPreferences.getString("gas_stations", "[]")
    val gasStationsList = gson.fromJson(gasStationsJson, Array<GasStationInfo>::class.java).toMutableList()

    // Adiciona o novo posto à lista
    gasStationsList.add(newStation)

    // Salva novamente a lista atualizada
    val updatedJson = gson.toJson(gasStationsList)
    val editor = sharedPreferences.edit()
    editor.putString("gas_stations", updatedJson)
    editor.apply()

    Log.d("GasStations", "Posto salvo: $stationName, $gasolinePrice, $alcoholPrice, $consumption")
}

@Composable
// Função para recuperar a lista de postos de gasolina
fun getGasStations(context: Context): List<GasStationInfo> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("GasStationPrefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("gas_stations", "[]") // Retorna uma string vazia se não houver dados

    // Verificando se o json retornado é válido
    if (json == null || json.isEmpty()) {
        Log.d("GasStations", "Nenhum posto encontrado no SharedPreferences.")
        return emptyList()  // Retorna uma lista vazia caso o json esteja vazio ou inválido
    }

    val type = object : TypeToken<List<GasStationInfo>>() {}.type // Definindo corretamente o tipo
    val stations = gson.fromJson<List<GasStationInfo>>(json, type)  // Explicitamente informando o tipo

    // Log para verificar o conteúdo da lista
    Log.d("GasStations", "Postos carregados: $stations")

    return stations // Converte o JSON de volta para uma lista de objetos
}

// Função para buscar um posto pelo ID
@Composable
fun getGasStationById(context: Context, stationId: Int): GasStationInfo? {
    val gasStations = getGasStations(context)
    return gasStations.find { it.id == stationId }
}

@Composable
fun updateGasStation(context: Context, stationId: Int, newStationName: String?, newGasolinePrice: String, newAlcoholPrice: String, newConsumption: Boolean) {
    val sharedPreferences = context.getSharedPreferences("gasStations", Context.MODE_PRIVATE)
    val gasStationsJson = sharedPreferences.getString("gasStationsList", "[]")
    val gasStationsList = gson.fromJson(gasStationsJson, Array<GasStationInfo>::class.java).toMutableList()

    // Encontrar o posto com o ID fornecido
    val gasStation = getGasStationById(context, stationId = stationId)

    if (gasStation != null) {
        // Atualiza os dados do posto
        val updatedStation = gasStation.copy(
            stationName = newStationName,
            gasolinePrice = newGasolinePrice,
            alcoholPrice = newAlcoholPrice,
            consumption = newConsumption
        )

        // Substituir o posto atualizado na lista
        val index = gasStationsList.indexOf(gasStation)
        gasStationsList[index] = updatedStation

        // Salvar a lista atualizada de postos
        val updatedJson = gson.toJson(gasStationsList)
        sharedPreferences.edit().putString("gasStationsList", updatedJson).apply()
    }
}

// Data class para armazenar as informações
data class GasStationInfo(
    val id: Int,  // ID único para cada posto
    val stationName: String?,
    val gasolinePrice: String,
    val alcoholPrice: String,
    val consumption: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null
)