package com.example.interfaz_api_gestordetareas.Navigation

//Rutas de navegacion
sealed class AppScreen(val route: String) {
    object InicioSesionScreen : AppScreen("InicioSesionScreen")
    object RegistroUsuarioScreen : AppScreen("RegistroUsuarioScreen")
    object tarea_screen_user : AppScreen("tareas_screen_user")
    object tarea_screen_admin : AppScreen("tareas_screen_admin")
}
