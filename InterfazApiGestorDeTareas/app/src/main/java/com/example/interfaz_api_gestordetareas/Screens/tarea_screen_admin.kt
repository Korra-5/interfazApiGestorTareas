package com.example.interfaz_api_gestordetareas.Screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

//Pantalla que se muestra a los usuarios con rol ADMIN
@Composable
fun tarea_screen_admin(){
    TareasAppAdmin(apiService)
}