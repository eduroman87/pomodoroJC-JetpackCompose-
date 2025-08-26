package com.eduroman87.pomodorojc.ui

// Importación de funciones y tipos necesarios para la navegación
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Importación de las pantallas que se usarán en la navegación
import com.eduroman87.pomodorojc.ui.screens.SetupScreen
import com.eduroman87.pomodorojc.ui.screens.PomodoroScreen

// Importación del tipo de estado usado por PomodoroScreen
import com.eduroman87.pomodorojc.viewmodel.PomodoroViewModel

/**
 * Composable que define el sistema de navegación de la aplicación Pomodoro.
 * Utiliza NavHost para gestionar el flujo entre SetupScreen y PomodoroScreen,
 * pasando parámetros necesarios para configurar el temporizador.
 */
@Composable
fun PomodoroNavHost() {
    // Controlador que gestiona la navegación entre composables
    val navController = rememberNavController()

    // Contenedor de navegación con la pantalla inicial "setup"
    NavHost(navController = navController, startDestination = "setup") {

        // Pantalla inicial donde el usuario configura su sesión Pomodoro
        composable("setup") {
            SetupScreen { cycles, work, rest ->
                // Al confirmar los datos, se navega a la pantalla principal
                // pasando los parámetros como parte de la ruta
                navController.navigate("pomodoro/$cycles/$work/$rest")
            }
        }

        // Pantalla principal del temporizador Pomodoro
        composable(
            route = "pomodoro/{cycles}/{work}/{rest}", // Ruta con parámetros dinámicos
            arguments = listOf(
                navArgument("cycles") { type = NavType.IntType }, // Número de ciclos
                navArgument("work") { type = NavType.IntType },   // Minutos de trabajo
                navArgument("rest") { type = NavType.IntType }    // Minutos de descanso
            )
        ) { backStackEntry ->
            // Recuperación de los parámetros pasados desde SetupScreen
            val cycles = backStackEntry.arguments?.getInt("cycles")
            val work = backStackEntry.arguments?.getInt("work")
            val rest = backStackEntry.arguments?.getInt("rest")

            // Instancia del ViewModel que gestiona el estado del temporizador
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<PomodoroViewModel>()

            // Flag para evitar múltiples inicializaciones del ViewModel
            val alreadyInitialized = remember(viewModel) { mutableStateOf(false) }

            // Inicializa el ViewModel solo una vez con los parámetros recibidos
            if (!alreadyInitialized.value && cycles != null && work != null && rest != null) {
                viewModel.initialize(
                    workMillis = work.toLong(),
                    restMillis = rest.toLong(),
                    cycles = cycles
                )
                alreadyInitialized.value = true
            }

            // Observación del estado actual del temporizador desde el ViewModel
            val timeLeft = viewModel.timeLeft.collectAsState().value
            val currentState = viewModel.currentState.collectAsState().value
            val currentCycle = viewModel.currentCycle.collectAsState().value

            // Renderizado de la pantalla Pomodoro solo si los parámetros son válidos
            if (cycles != null && work != null && rest != null) {
                val workInSeconds = work
                val restInSeconds = rest

                PomodoroScreen(
                    currentState = currentState,
                    currentCycle = currentCycle,
                    totalCycles = cycles,
                    workDuration = workInSeconds,
                    restDuration = restInSeconds,
                    onStart = { viewModel.start() },      // Inicia el temporizador
                    onPause = { viewModel.pause() },      // Pausa el temporizador
                    resetToSetup = {
                        viewModel.reset()                // Reinicia el estado del ViewModel
                        navController.navigate("setup") {
                            // Elimina la pantalla actual del back stack
                            popUpTo("pomodoro") { inclusive = true }
                        }
                    },
                    viewModel = viewModel                // Se pasa el ViewModel para acceso directo
                )
            }
        }
    }
}
