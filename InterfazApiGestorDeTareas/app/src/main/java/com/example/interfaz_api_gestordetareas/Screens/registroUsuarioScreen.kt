package com.example.interfaz_api_gestordetareas.Screens

import UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.interfaz_api_gestordetareas.R
import com.example.interfaz_api_gestordetareas.Utils.ErrorUtils

@Composable
fun RegistroUsuarioScreen(navController: NavController, viewModel: UserViewModel) {
    // Estados para los campos del formulario
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }

    // Estados para validación
    val errorFields = remember { mutableStateMapOf<String, String>() }
    var isLoading by remember { mutableStateOf(false) }
    var generalError by remember { mutableStateOf<String?>(null) }

    // Observar estado de registro
    val registroState by viewModel.registroState.observeAsState()

    // Efecto para manejar cambios en el estado de registro
    LaunchedEffect(registroState) {
        when (registroState) {
            is RegistroState.Loading -> {
                isLoading = true
                generalError = null
            }
            is RegistroState.Success -> {
                isLoading = false
                // Navegar de vuelta a login con indicador de éxito
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("registro_exitoso", true)
                navController.popBackStack()
            }
            is RegistroState.Error -> {
                isLoading = false
                generalError = ErrorUtils.parseErrorMessage((registroState as RegistroState.Error).message)
            }
            else -> {
                isLoading = false
            }
        }
    }

    // Función para validar campos
    fun validateFields(): Boolean {
        errorFields.clear()

        // Validar usuario
        if (username.trim().isEmpty()) {
            errorFields["username"] = "El usuario es requerido"
        } else if (username.trim().length < 4 || username.trim().length > 30) {
            errorFields["username"] = "El usuario debe tener entre 4 y 30 caracteres"
        }

        // Validar contraseña
        if (password.isEmpty()) {
            errorFields["password"] = "La contraseña es requerida"
        } else if (password.length < 5 || password.length > 30) {
            errorFields["password"] = "La contraseña debe tener entre 5 y 30 caracteres"
        }

        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            errorFields["confirmPassword"] = "Debe repetir la contraseña"
        } else if (password != confirmPassword) {
            errorFields["confirmPassword"] = "Las contraseñas no coinciden"
        }

        // Validar email
        if (email.trim().isEmpty()) {
            errorFields["email"] = "El email es requerido"
        } else if (!Regex("^[\\w\\.-]+@([\\w\\-]+\\.)+(com|org|net|es|terra|gmail)$").matches(email)) {
            errorFields["email"] = "Formato de email inválido"
        }

        // Validar municipio
        if (municipio.trim().isEmpty()) {
            errorFields["municipio"] = "El municipio es requerido"
        }

        // Validar provincia
        if (provincia.trim().isEmpty()) {
            errorFields["provincia"] = "La provincia es requerida"
        }

        // Validar calle
        if (calle.trim().isEmpty()) {
            errorFields["calle"] = "La calle es requerida"
        }

        // Validar número
        if (numero.trim().isEmpty()) {
            errorFields["numero"] = "El número es requerido"
        }

        return errorFields.isEmpty()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Crear una cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.azulPrimario),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Completa tus datos para registrarte",
                fontSize = 16.sp,
                color = colorResource(R.color.textoSecundario),
                modifier = Modifier.padding(bottom = 30.dp),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.white)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo de usuario
                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Usuario",
                        icon = R.drawable.ic_user,
                        errorMessage = errorFields["username"],
                        iconSize = 20.dp
                    )

                    // Campo de contraseña
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        icon = R.drawable.ic_lock,
                        isPassword = true,
                        errorMessage = errorFields["password"],
                        iconSize = 20.dp
                    )

                    // Campo de repetir contraseña
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Repetir Contraseña",
                        icon = R.drawable.ic_lock,
                        isPassword = true,
                        errorMessage = errorFields["confirmPassword"],
                        iconSize = 20.dp
                    )

                    // Campo de email
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        icon = R.drawable.ic_email,
                        keyboardType = KeyboardType.Email,
                        errorMessage = errorFields["email"],
                        iconSize = 20.dp
                    )

                    // Sección de dirección
                    Text(
                        text = "Información de dirección",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.textoPrimario),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Start
                    )

                    // Campo de municipio
                    CustomTextField(
                        value = municipio,
                        onValueChange = { municipio = it },
                        label = "Municipio",
                        icon = R.drawable.ic_location,
                        errorMessage = errorFields["municipio"],
                        iconSize = 20.dp
                    )

                    // Campo de provincia
                    CustomTextField(
                        value = provincia,
                        onValueChange = { provincia = it },
                        label = "Provincia",
                        icon = R.drawable.ic_location,
                        errorMessage = errorFields["provincia"],
                        iconSize = 20.dp
                    )

                    // Campo de calle
                    CustomTextField(
                        value = calle,
                        onValueChange = { calle = it },
                        label = "Calle",
                        icon = R.drawable.ic_street,
                        errorMessage = errorFields["calle"],
                        iconSize = 20.dp
                    )

                    // Campo de número
                    CustomTextField(
                        value = numero,
                        onValueChange = { numero = it },
                        label = "Número",
                        icon = R.drawable.ic_number,
                        keyboardType = KeyboardType.Number,
                        errorMessage = errorFields["numero"],
                        iconSize = 20.dp
                    )

                    // Mensaje de error general
                    if (generalError != null) {
                        Text(
                            text = generalError!!,
                            color = colorResource(R.color.error),
                            modifier = Modifier.padding(vertical = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Botón de registro
                    Button(
                        onClick = {
                            if (validateFields()) {
                                viewModel.registrarUsuario(
                                    username = username.trim(),
                                    password = password,
                                    passwordRepeat = confirmPassword,
                                    email = email.trim(),
                                    rol = "USER",
                                    municipio = municipio.trim(),
                                    provincia = provincia.trim(),
                                    calle = calle.trim(),
                                    numero = numero.trim()
                                )
                            } else {
                                generalError = "Por favor, corrige los errores en el formulario"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.azulPrimario),
                            disabledContainerColor = colorResource(R.color.cyanSecundario),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = colorResource(R.color.white),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Registrarse", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Enlace a login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? ",
                    color = colorResource(R.color.textoSecundario)
                )

                TextButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.azulPrimario)
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = colorResource(R.color.azulPrimario),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Int,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    iconSize: Dp = 20.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(iconSize),
                    tint = colorResource(R.color.azulPrimario)
                )
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.azulPrimario),
                unfocusedBorderColor = colorResource(R.color.cyanSecundario),
                errorBorderColor = colorResource(R.color.error),
                focusedLabelColor = colorResource(R.color.azulPrimario),
                unfocusedLabelColor = colorResource(R.color.textoSecundario),
                focusedTextColor = colorResource(R.color.black),
                unfocusedTextColor = colorResource(R.color.black),
                errorTextColor = colorResource(R.color.black)
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = colorResource(R.color.error),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}