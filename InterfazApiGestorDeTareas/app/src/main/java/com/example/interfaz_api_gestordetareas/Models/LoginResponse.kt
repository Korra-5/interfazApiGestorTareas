package com.example.interfaz_api_gestordetareas.Models

//Plantilla para login exitoso
data class LoginResponse(
    val token: String,
    // Otros posibles campos si los hay
    val message: String? = null
)