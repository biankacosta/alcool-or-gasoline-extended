package com.example.alcoolougasolina

sealed class Screens (val screens: String) {
    data object List: Screens("list")
    data object Calcular: Screens("calcular")
}