package com.example.interfaz_api_gestordetareas.Screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interfaz_api_gestordetareas.Data.RetrofitService
import com.example.interfaz_api_gestordetareas.Models.Tarea
import com.example.interfaz_api_gestordetareas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val apiService = RetrofitService.RetrofitServiceFactory.makeRetrofitService()

//Pantalla que se muestra a los usuarios con rol USER
@Composable
fun tarea_screen_user() {
TareasApp(apiService)
}

// Se utiliza para notificar al usuario de alguna acción o error.
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasApp(service: RetrofitService) {
    // Se obtiene el contexto actual y extrae el token y nombre de usuario
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("TOKEN", "") ?: ""
    val username = sharedPreferences.getString("USERNAME", "") ?: ""
    val bearerToken = "Bearer $token"

    // Estados para gestionar la lista de tareas, indicador de carga, mensaje de error y trigger para refrescar
    var tareas by remember { mutableStateOf<List<Tarea>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshList by remember { mutableStateOf(0) }

    // Función para refrescar las tareas
    val refreshTareas = {
        refreshList += 1
    }

    // Se refresca cada vez que se añade algo a la refreshList
    LaunchedEffect(key1 = refreshList) {
        try {
            isLoading = true
            // Solo se procede si se cuenta con un token válido
            if (token.isNotEmpty()) {
                // Realiza la petición en un hilo IO para obtener las tareas del usuario
                val response = withContext(Dispatchers.IO) {
                    service.getTarea(bearerToken, username)
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

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.cyanSecundario).copy(alpha = 0.3f))  // Fondo transparente
    ) {
        Scaffold(
            topBar = {
                // Barra superior con el título "Mis Tareas"
                TopAppBar(
                    title = {
                        Text(
                            "Mis Tareas",
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
                when {
                    // Indica que se está esperando la respuesta de la API
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
                    // Muestra un mensaje de error en una Card
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
                    // Si la lista de tareas está vacía, muestra un mensaje indicativo
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
                    // Si hay tareas disponibles, se listan usando LazyColumn
                    else -> {
                        LazyColumn {
                            items(tareas, key = { it.id ?: it.titulo }) { tarea ->
                                TareaCard(
                                    tarea = tarea,
                                    bearerToken = bearerToken,
                                    service = service,
                                    // Actualiza la tarea completada en la lista
                                    onTaskCompleted = { updatedTask ->
                                        tareas = tareas.map { if (it.id == updatedTask.id) updatedTask else it }
                                    },
                                    // Elimina la tarea que se haya borrado de la lista
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

        AgregarTareaLista(
            service = service,
            isAdmin = false,
            onTaskAdded = refreshTareas
        )
    }
}

@Composable
fun TareaCard(
    tarea: Tarea,
    bearerToken: String,
    service: RetrofitService,
    onTaskCompleted: (Tarea) -> Unit,
    onTaskDeleted: (Tarea) -> Unit
) {
    // Se obtiene el contexto actual para mostrar Toasts (Si se necesita)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Tarjeta principal que agrupa la información de la tarea.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_colors)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador visual del estado de la tarea (completada o pendiente).
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (tarea.completada)
                            colorResource(R.color.correcto).copy(alpha = 0.2f)
                        else
                            colorResource(R.color.error).copy(alpha = 0.1f)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Botón para marcar la tarea como completada.
                IconButton(
                    onClick = {
                        // Se usa una coroutine para llamar a la API.
                        coroutineScope.launch {
                            try {
                                val response = service.completarTarea(bearerToken, tarea.id ?: "")
                                if (response.isSuccessful) {
                                    response.body()?.let { updatedTask ->
                                        onTaskCompleted(updatedTask)
                                    }
                                } else {
                                    showToast(context, "Error completando tarea")
                                }
                            } catch (e: Exception) {
                                showToast(context, "Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    // Icono que indica la acción de completar la tarea. (Este es el icono, el otro el box que lo contiene)
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completar tarea",
                        tint = if (tarea.completada)
                            colorResource(R.color.correcto)
                        else
                            colorResource(R.color.textoSecundario)
                    )
                }
            }

            // Muestra título, descripción y estado de la tarea.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.textoPrimario)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tarea.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.textoPrimario)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (tarea.completada) "Completada" else "Pendiente",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (tarea.completada)
                        colorResource(R.color.correcto)
                    else
                        colorResource(R.color.error)
                )
            }

            // Botón para borrar la tarea.
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            // Llama a la API para borrar la tarea.
                            val response = service.borrarTarea(bearerToken, tarea.id ?: "")
                            if (response.isSuccessful) {
                                onTaskDeleted(tarea)
                            } else {
                                showToast(context, "Error borrando tarea")
                            }
                        } catch (e: Exception) {
                            showToast(context, "Error: ${e.message}")
                        }
                    }
                }
            ) {
                // Icono representa borrar.
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar tarea",
                    tint = colorResource(R.color.textoSecundario)
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaLista(
    service: RetrofitService,
    isAdmin: Boolean = false,
    onTaskAdded: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("TOKEN", "") ?: ""
    val currentUsername = sharedPreferences.getString("USERNAME", "") ?: ""
    val bearerToken = "Bearer $token"

    var showDialog by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var assignedUsername by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Contenedor para ubicar el botón flotante en la esquina inferior derecha
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Botón flotante que al pulsar muestra el dialog para agregar una tarea
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.padding(16.dp),
            containerColor = colorResource(R.color.azulPrimario),
            contentColor = colorResource(R.color.white)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir Tarea"
            )
        }
    }

    // Si showDialog es true se muestra un AlertDialog para añadir una nueva tarea
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Cierra el diálogo al tocar afuera
            title = {
                Text(
                    "Añadir Nueva Tarea",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(R.color.textoPrimario)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Se ejecuta en una coroutine la llamada a la API para crear la tarea
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                // Construir el objeto Tarea con los datos ingresados
                                val nuevaTarea = Tarea(
                                    id = null,
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    completada = false,
                                    // Si es administrador se permite asignar un usuario; de lo contrario se usa el usuario actual.
                                    usuario = if (isAdmin) assignedUsername else currentUsername,
                                    fechaCreacion = null
                                )

                                // Llamada a la API para crear la tarea en un hilo IO
                                val response = withContext(Dispatchers.IO) {
                                    service.crearTarea(bearerToken, nuevaTarea)
                                }

                                // Si la respuesta es exitosa se resetean los datos
                                if (response.isSuccessful) {
                                    showDialog = false
                                    titulo = ""
                                    descripcion = ""
                                    assignedUsername = ""
                                    onTaskAdded()
                                } else {
                                    showToast(context, "Error: Revisa que el usuario exista")
                                }
                            } catch (e: Exception) {
                                showToast(context, "Error: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    // Se habilita el botón solo si no está cargando y se han completado los datos esenciales.
                    enabled = !isLoading && titulo.isNotEmpty() && (isAdmin && assignedUsername.isNotEmpty() || !isAdmin),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.azulPrimario),
                        disabledContainerColor = colorResource(R.color.cyanSecundario),
                        contentColor = colorResource(R.color.white),
                        disabledContentColor = colorResource(R.color.textoSecundario)
                    )
                ) {
                    Text("Guardar")
                }
            },
            // Botón para cancelar la operación y cerrar el dialog
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.textoPrimario)
                    )
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = colorResource(R.color.white),
            text = {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Campo de texto para el Título
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título", color = colorResource(R.color.textoPrimario)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = colorResource(R.color.textoPrimario),
                            focusedTextColor = colorResource(R.color.textoPrimario),
                            cursorColor = colorResource(R.color.azulPrimario),
                            focusedBorderColor = colorResource(R.color.azulPrimario),
                            unfocusedBorderColor = colorResource(R.color.cyanSecundario),
                            unfocusedLabelColor = colorResource(R.color.textoSecundario),
                            focusedLabelColor = colorResource(R.color.azulPrimario)
                        )
                    )
                    // Campo de texto para la Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción", color = colorResource(R.color.textoPrimario)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = colorResource(R.color.textoPrimario),
                            focusedTextColor = colorResource(R.color.textoPrimario),
                            cursorColor = colorResource(R.color.azulPrimario),
                            focusedBorderColor = colorResource(R.color.azulPrimario),
                            unfocusedBorderColor = colorResource(R.color.cyanSecundario),
                            unfocusedLabelColor = colorResource(R.color.textoSecundario),
                            focusedLabelColor = colorResource(R.color.azulPrimario)
                        )
                    )
                    // Si el usuario es administrador, puede asignar la tarea a otro usuario
                    if (isAdmin) {
                        OutlinedTextField(
                            value = assignedUsername,
                            onValueChange = { assignedUsername = it },
                            label = { Text("Usuario asignado", color = colorResource(R.color.textoPrimario)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = colorResource(R.color.textoPrimario),
                                focusedTextColor = colorResource(R.color.textoPrimario),
                                cursorColor = colorResource(R.color.azulPrimario),
                                focusedBorderColor = colorResource(R.color.azulPrimario),
                                unfocusedBorderColor = colorResource(R.color.cyanSecundario),
                                unfocusedLabelColor = colorResource(R.color.textoSecundario),
                                focusedLabelColor = colorResource(R.color.azulPrimario)
                            )
                        )
                    }
                    // Si la tarea se está guardando, se muestra un indicador de carga
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            color = colorResource(R.color.azulPrimario)
                        )
                    }
                }
            }
        )
    }
}
