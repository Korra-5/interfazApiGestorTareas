package com.example.interfaz_api_gestordetareas.Models

import java.util.Date
import com.google.gson.annotations.SerializedName

//Clase tarea
data class Tarea(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("completada")
    val completada: Boolean,
    @SerializedName("fechaCreacion")
    val fechaCreacion: Date?,
    @SerializedName("usuario")
    val usuario: String
)
