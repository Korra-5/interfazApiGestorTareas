package com.example.interfaz_api_gestordetareas.Models

//Estados de los registros y logins

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    object Success : RegistroState()
    data class Error(val message: String) : RegistroState()
}