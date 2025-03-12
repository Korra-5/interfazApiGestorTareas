package com.example.interfaz_api_gestordetareas

import AppNavigation
import UserViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.interfaz_api_gestordetareas.ui.theme.InterfazApiGestorDeTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InterfazApiGestorDeTareasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(Modifier.padding(innerPadding)){
                    AppNavigation(UserViewModel())
                }
                }
            }
        }
    }
}
