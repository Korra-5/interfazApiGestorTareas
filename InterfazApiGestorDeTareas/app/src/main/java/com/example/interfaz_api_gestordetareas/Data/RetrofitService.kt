package com.example.interfaz_api_gestordetareas.Data

import com.example.interfaz_api_gestordetareas.Models.LoginResponse
import com.example.interfaz_api_gestordetareas.Models.RegistroResponse
import com.example.interfaz_api_gestordetareas.Models.Tarea
import com.example.interfaz_api_gestordetareas.Models.UsuarioLoginDTO
import com.example.interfaz_api_gestordetareas.Models.UsuarioRegisterDTO
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

//Conexion con la API
interface RetrofitService {

    @POST("/Usuario/register")
    suspend fun insertUser(
        @Body usuario: UsuarioRegisterDTO
    ): Response<RegistroResponse>

    @POST("/Usuario/login")
    suspend fun loginUser(
        @Body usuario: UsuarioLoginDTO
    ): Response<LoginResponse>

    @GET("/Tarea/verTarea/{username}")
    suspend fun getTarea(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<List<Tarea>>

    @GET("/Tarea/verTareas")
    suspend fun getTareas(
        @Header("Authorization") token: String,
    ): Response<List<Tarea>>

    @GET("/Tarea/verTareas")
    suspend fun verTareas(
        @Header("Authorization") token: String,
    ): Response<List<Tarea>>

    @PUT("/Tarea/completarTarea/{id}")
    suspend fun completarTarea(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Tarea>

    @DELETE("/Tarea/borrarTarea/{id}")
    suspend fun borrarTarea(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Tarea>

    @POST("/Tarea/crearTarea")
    suspend fun crearTarea(
        @Header("Authorization") token: String,
        @Body tarea: Tarea
    ): Response<Tarea>

    /*
    FACTORY USADA DURANTE EL DESARROLLO DE LA APP PARA COMRPOBAR ERRORES MEDIANTE LOG Y DEBUG
     */
    object RetrofitServiceFactory {
        fun makeRetrofitService(): RetrofitService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl("https://api-mongodb-4.onrender.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)
        }
    }
}