package com.eduroman87pomodorojc

// Importaciones necesarias para ciclo de vida, UI y ViewModel
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.eduroman87.pomodorojc.ui.PomodoroNavHost
import com.eduroman87.pomodorojc.ui.screens.PomodoroScreen
import com.eduroman87.pomodorojc.ui.theme.AppTheme
import com.eduroman87.pomodorojc.viewmodel.PomodoroViewModel

/**
 * Actividad principal de la aplicación Pomodoro.
 * Se encarga de inicializar la interfaz usando Jetpack Compose.
 * El flujo de navegación comienza directamente en la pantalla SetupScreen,
 * desde donde el usuario puede configurar su sesión personalizada.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se fuerza la orientación vertical para mantener consistencia en la experiencia de usuario.
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // setContent establece el contenido visual de la app usando Jetpack Compose.
        setContent {
            // AppTheme aplica el tema visual global (colores, tipografía, estilos).
            AppTheme {
                // PomodoroNavHost gestiona el flujo de navegación entre pantallas:
                // SetupScreen → PomodoroScreen, según la configuración del usuario.
                PomodoroNavHost()
            }
        }
    }
}
