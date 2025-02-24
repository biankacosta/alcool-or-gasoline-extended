package com.example.alcoolougasolina

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

val gson = Gson()

// Função para gerar UUIDs únicos
fun generateUniqueId(): String = UUID.randomUUID().toString()

// Função para salvar um posto de gasolina
fun addNewGasStation(
    context: Context,
    stationName: String?,
    gasolinePrice: Float,
    alcoholPrice: Float,
    consumption: Boolean,
    latitude: String?,
    longitude: String?
) {
    val sharedPreferences = context.getSharedPreferences("gas_stations", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("gas_stations", "[]") ?: "[]"

    val type = object : TypeToken<List<GasStationInfo>>() {}.type
    val gasStationsList: MutableList<GasStationInfo> = gson.fromJson(json, type)

    val newStation = GasStationInfo(
        id = generateUniqueId(),
        stationName = stationName,
        gasolinePrice = gasolinePrice,
        alcoholPrice = alcoholPrice,
        consumption = consumption,
        latitude = latitude,
        longitude = longitude,
        registrationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) // Data atual
    )

    gasStationsList.add(newStation)

    val updatedJson = gson.toJson(gasStationsList)
    sharedPreferences.edit().putString("gas_stations", updatedJson).apply()

    Log.d("GasStations", "Posto adicionado: $newStation")
}

// Função para recuperar a lista de postos de gasolina
fun getGasStations(context: Context): List<GasStationInfo> {
    val sharedPreferences = context.getSharedPreferences("gas_stations", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("gas_stations", "[]") ?: "[]"

    val type = object : TypeToken<List<GasStationInfo>>() {}.type
    val stations: List<GasStationInfo> = gson.fromJson(json, type)

    Log.d("GasStations", "Postos carregados: $stations")
    return stations
}

// Função para buscar um posto pelo ID
fun getGasStationById(context: Context, stationId: String): GasStationInfo? {
    val gasStations = getGasStations(context)
    return gasStations.find { it.id == stationId }
}

// Função para atualizar um posto de gasolina
fun updateGasStation(
    context: Context,
    stationId: String,
    newStationName: String?,
    newGasolinePrice: Float,
    newAlcoholPrice: Float,
    newConsumption: Boolean
) {
    val sharedPreferences = context.getSharedPreferences("gas_stations", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("gas_stations", "[]") ?: "[]"

    val type = object : TypeToken<List<GasStationInfo>>() {}.type
    val gasStationsList: MutableList<GasStationInfo> = gson.fromJson(json, type)

    val index = gasStationsList.indexOfFirst { it.id == stationId }

    if (index != -1) {
        val updatedStation = gasStationsList[index].copy(
            stationName = newStationName,
            gasolinePrice = newGasolinePrice,
            alcoholPrice = newAlcoholPrice,
            consumption = newConsumption
        )

        gasStationsList[index] = updatedStation

        val updatedJson = gson.toJson(gasStationsList)
        sharedPreferences.edit().putString("gas_stations", updatedJson).apply()
    }
}

// Função para excluir um posto de gasolina
fun deleteGasStation(context: Context, stationId: String) {
    val sharedPreferences = context.getSharedPreferences("gas_stations", Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("gas_stations", "[]") ?: "[]"

    val type = object : TypeToken<List<GasStationInfo>>() {}.type
    val gasStationsList: MutableList<GasStationInfo> = gson.fromJson(json, type)

    val updatedList = gasStationsList.filter { it.id != stationId }

    val updatedJson = gson.toJson(updatedList)
    sharedPreferences.edit().putString("gas_stations", updatedJson).apply()

    Log.d("GasStations", "Posto removido: $stationId")
}


// Data class para armazenar as informações
data class GasStationInfo(
    val id: String,
    val stationName: String?,
    val gasolinePrice: Number,
    val alcoholPrice: Number,
    val consumption: Boolean,
    val latitude: String?,
    val longitude: String?,
    val registrationDate: String // Data de cadastro
)
