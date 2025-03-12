import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.interfaz_api_gestordetareas.Navigation.AppScreen
import com.example.interfaz_api_gestordetareas.Screens.RegistroUsuarioScreen
import com.example.interfaz_api_gestordetareas.Screens.tarea_screen_admin
import com.example.interfaz_api_gestordetareas.Screens.tarea_screen_user

//App navigation
@Composable
fun AppNavigation(viewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.InicioSesionScreen.route) {
        composable(AppScreen.InicioSesionScreen.route) { InicioSesionScreen(navController, viewModel) }
        composable(AppScreen.RegistroUsuarioScreen.route){ RegistroUsuarioScreen(navController, viewModel) }
        composable(AppScreen.tarea_screen_user.route) { tarea_screen_user() }
        composable(AppScreen.tarea_screen_admin.route) { tarea_screen_admin() }

    }
}
