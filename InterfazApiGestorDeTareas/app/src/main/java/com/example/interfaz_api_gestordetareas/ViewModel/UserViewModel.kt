import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interfaz_api_gestordetareas.Data.RetrofitService
import com.example.interfaz_api_gestordetareas.Models.Direccion
import com.example.interfaz_api_gestordetareas.Models.RegistroResponse
import com.example.interfaz_api_gestordetareas.Models.UsuarioLoginDTO
import com.example.interfaz_api_gestordetareas.Models.UsuarioRegisterDTO
import kotlinx.coroutines.launch
import android.util.Log
import android.util.Base64
import org.json.JSONObject


// Estados para representar los resultados de las operaciones
sealed class RegistroState {
    object Loading : RegistroState()
    data class Success(val data: RegistroResponse) : RegistroState()
    data class Error(val code: Int, val message: String) : RegistroState()
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val token: String, val role: String?) : LoginState()
    data class Error(val code: Int, val message: String) : LoginState()
}

class UserViewModel : ViewModel() {
    private val apiService = RetrofitService.RetrofitServiceFactory.makeRetrofitService()

    // Estados que la UI puede observar
    private val _registroState = MutableLiveData<RegistroState>()
    val registroState: LiveData<RegistroState> = _registroState

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> = _mensajeError

    private val _tokenLogin = MutableLiveData<String>()
    val tokenLogin: LiveData<String> = _tokenLogin

    // Nuevo LiveData para el rol del usuario
    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    fun registrarUsuario(
        username: String,
        email: String,
        password: String,
        passwordRepeat: String,
        rol: String,
        municipio: String,
        provincia: String,
        calle: String,
        numero: String
    ) {
        _registroState.value = RegistroState.Loading

        viewModelScope.launch {
            try {
                val usuario = UsuarioRegisterDTO(
                    username = username,
                    email = email,
                    password = password,
                    passwordRepeat = passwordRepeat,
                    rol = rol,
                    direccion = Direccion(
                        municipio = municipio,
                        provincia = provincia,
                        calle = calle,
                        numero = numero
                    )
                )

                val response = apiService.insertUser(usuario)

                if (response.isSuccessful) {
                    _registroState.value = RegistroState.Success(response.body()!!)
                    _registroExitoso.value = true
                    _mensajeError.value = ""
                } else {
                    val mensaje = "Error: ${response.code()} - ${response.errorBody()?.string() ?: "Error desconocido"}"
                    _registroState.value = RegistroState.Error(response.code(), mensaje)
                    _mensajeError.value = mensaje
                    _registroExitoso.value = false
                }
            } catch (e: Exception) {
                val mensaje = "Error: ${e.message ?: "Error de conexión"}"
                _registroState.value = RegistroState.Error(-1, mensaje)
                _mensajeError.value = mensaje
                _registroExitoso.value = false
            }
        }
    }

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val usuario = UsuarioLoginDTO(
                    username = username,
                    password = password
                )

                val response = apiService.loginUser(usuario)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val token = loginResponse.token

                        // Decodificar el token para obtener el rol
                        val (_, role) = decodeJwt(token)
                        Log.d("UserViewModel", "Token decodificado: usuario=$username, rol=$role")

                        // Guardar el rol
                        _userRole.value = role ?: "USER" // Por defecto USER si no se puede determinar

                        // Actualizar estados
                        _loginState.value = LoginState.Success(token, role)
                        _tokenLogin.value = token
                    } ?: run {
                        val errorMsg = "Respuesta vacía del servidor"
                        _loginState.value = LoginState.Error(
                            response.code(),
                            errorMsg
                        )
                        _tokenLogin.value = ""
                        _userRole.value = ""
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Credenciales incorrectas"
                        404 -> "Usuario no encontrado"
                        500 -> "Error del servidor"
                        else -> response.errorBody()?.string() ?: "Error de autenticación"
                    }

                    _loginState.value = LoginState.Error(
                        response.code(),
                        errorMsg
                    )
                    _tokenLogin.value = ""
                    _userRole.value = ""
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("timeout") == true -> "Tiempo de espera agotado"
                    e.message?.contains("Unable to resolve host") == true -> "Sin conexión a internet"
                    else -> e.message ?: "Error de conexión"
                }

                _loginState.value = LoginState.Error(
                    -1,
                    errorMsg
                )
                _tokenLogin.value = ""
                _userRole.value = ""
            }
        }
    }
    private fun decodeJwt(token: String): Pair<String, String?> {
        try {
            // Dividir el token en sus partes
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JWT_DECODE", "Token inválido: no tiene 3 partes (Imposible)")
                return Pair("", null)
            }

            // Decodificar la parte del payload (segunda parte)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedPayload = String(decodedBytes)

            // Extraer información del payload
            val jsonPayload = JSONObject(decodedPayload)
            val username = jsonPayload.optString("sub", "")
            val roles = jsonPayload.optString("roles", "")

            Log.d("JWT_DECODE", "Username: $username, Roles: $roles")

            val role = when {
                roles.contains("ROLE_ADMIN") -> "ADMIN"
                roles.contains("ADMIN") -> "ADMIN"
                roles.contains("ROLE_USER") -> "USER"
                roles.contains("USER") -> "USER"
                else -> null
            }

            return Pair(username, role)
        } catch (e: Exception) {
            Log.e("JWT_DECODE", "Error decodificando token: ${e.message}")
            return Pair("", null)
        }
    }
}