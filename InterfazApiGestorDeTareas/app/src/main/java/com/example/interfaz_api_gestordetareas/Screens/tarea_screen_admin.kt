package com.example.interfaz_api_gestordetareas.Screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.interfaz_api_gestordetareas.Data.RetrofitService
import com.example.interfaz_api_gestordetareas.Models.Tarea
import com.example.interfaz_api_gestordetareas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Pantalla que se muestra a los usuarios con rol ADMIN
@Composable
fun tarea_screen_admin(){
    TareasAppAdmin(apiService)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasAppAdmin(service: RetrofitService) {
    // Obtener el contexto y  extraer token y nombre de usuario
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("TOKEN", "") ?: ""
    val username = sharedPreferences.getString("USERNAME", "") ?: ""
    val bearerToken = "Bearer $token"

    // Estados para gestionar tareas, carga, errores y refrescar la lista
    var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshList by remember { mutableIntStateOf(0) }

    // Función para refrescar las tareas
    val refreshTareas = {
        refreshList += 1
    }

    // Realizar la llamada a la API cada vez que refreshList cambia
    LaunchedEffect(key1 = refreshList) {
        try {
            isLoading = true
            if (token.isNotEmpty()) {
                // Llamada en un hilo IO para obtener las tareas
                val response = withContext(Dispatchers.IO) {
                    service.verTareas(bearerToken)
                }
                if (response.isSuccessful) {
                    tareas = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.code()} - ${response.message()}"
                }
            } else {
                errorMessage = "Token vacío"
            }
        } catch (e: Exception) {
            errorMessage = "Error de conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Caja principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.cyanSecundario).copy(alpha = 0.3f))  // Fondo con transparencia
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Panel de Administración",
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.white)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(R.color.azulPrimario),
                        titleContentColor = colorResource(R.color.white)
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Mostrar indicadores según el estado actual de la carga o contenido
                when {
                    // Si se está cargando, mostrar un indicador de progreso
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(R.color.azulPrimario)
                            )
                        }
                    }
                    // Si hubo un error, mostrarlo dentro de una tarjeta
                    errorMessage.isNotEmpty() -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(R.color.error).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = colorResource(R.color.error),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    // Si no hay tareas disponibles, mostrar un mensaje informativo
                    tareas.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No hay tareas disponibles",
                                    fontSize = 18.sp,
                                    color = colorResource(R.color.textoSecundario)
                                )
                                Text(
                                    text = "Pulsa + para añadir una nueva tarea",
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.textoSecundario),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                    // En caso de tener tareas, se listan usando LazyColumn
                    else -> {
                        LazyColumn {
                            items(tareas, key = { it.id ?: it.titulo }) { tarea ->
                                TareaCard(
                                    tarea = tarea,
                                    bearerToken = bearerToken,
                                    service = service,
                                    // Al completar una tarea se actualiza el listado
                                    onTaskCompleted = { updatedTask ->
                                        tareas = tareas.map { if (it.id == updatedTask.id) updatedTask else it }
                                    },
                                    // Al eliminar una tarea se remueve del listado
                                    onTaskDeleted = { deletedTask ->
                                        tareas = tareas.filter { it.id != deletedTask.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Agregar una tarea
        AgregarTareaLista(
            service = service,
            isAdmin = true,
            onTaskAdded = refreshTareas
        )
    }
}
