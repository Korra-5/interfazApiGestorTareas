package com.example.interfaz_api_gestordetareas.Models

//Clase que registra al usuario
data class UsuarioRegisterDTO(
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val rol: String,
    val direccion: Direccion)
