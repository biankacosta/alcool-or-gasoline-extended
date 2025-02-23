package com.example.alcoolougasolina

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

object PermissionManager {

    fun checkLocationPermission(
        context: Context?,
        requestPermissionLauncher: ActivityResultLauncher<String>,
        onLocationReceived: (String?, String?) -> Unit
    ) {
        val activity = context as? Activity

        when {
            ContextCompat.checkSelfPermission(
                context ?: return,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permissão", "Permissão já concedida! Obtendo localização...")
                if (activity != null) {
                    getCurrentLocation(activity) { latitude, longitude ->
                        onLocationReceived(latitude, longitude)
                    }
                }
            }

            activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true -> {
                showPermissionRationaleDialog(context, requestPermissionLauncher)
                onLocationReceived(null, "Permissão necessária")
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                onLocationReceived(null, "Permissão solicitada")
            }
        }
    }

    private fun showPermissionRationaleDialog(
        context: Activity,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.permission_required_title))
            .setMessage(context.getString(R.string.permission_message))
            .setPositiveButton(context.getString(R.string.allow_access)) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(context.getString(R.string.deny_access)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Activity, callback: (String?, String?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("Localização", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    callback(location.latitude.toString(), location.longitude.toString())
                } else {
                    Log.d("Localização", "Localização não encontrada")
                    callback(null, "Localização não encontrada")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Localização", "Erro ao obter localização: ${e.message}", e)
                callback(null, "Erro ao obter localização: ${e.message}")
            }
    }
}