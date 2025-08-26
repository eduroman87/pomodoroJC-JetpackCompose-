package com.eduroman87.pomodorojc.ui.screens

// Importaciones esenciales: UI de Compose, animaciones, scroll y utilidades responsive
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.eduroman87.pomodorojc.domain.TimerState
import com.eduroman87.pomodorojc.ui.theme.*
import com.eduroman87pomodorojc.utils.responsiveHeight
import com.eduroman87pomodorojc.utils.responsiveWidth
import com.eduroman87.pomodorojc.viewmodel.PomodoroViewModel

/**
 * Pantalla principal del temporizador Pomodoro.
 * Muestra el estado actual, cronómetro, barra de progreso y controles de Start/Pause/Reset.
 *
 * @param currentState   Estado actual del temporizador (IDLE, WORKING, RESTING, PAUSED, FINISHED).
 * @param currentCycle   Índice del ciclo en curso (1…totalCycles).
 * @param totalCycles    Número total de ciclos configurados.
 * @param workDuration   Duración de cada fase de trabajo en segundos.
 * @param restDuration   Duración de cada fase de descanso en segundos.
 * @param onStart        Callback que se invoca al pulsar Start.
 * @param onPause        Callback que se invoca al pulsar Pause.
 * @param resetToSetup   Callback para volver a la pantalla de configuración.
 * @param viewModel      ViewModel que expone el estado y el tiempo restante.
 */
@Composable
fun PomodoroScreen(
    currentState : TimerState.State,
    currentCycle : Int,
    totalCycles  : Int,
    workDuration : Int,
    restDuration : Int,
    onStart      : () -> Unit,
    onPause      : () -> Unit,
    resetToSetup : () -> Unit,
    viewModel    : PomodoroViewModel
) {
    // 1) Obtiene el tiempo restante (ms) y evita valores negativos
    val observedTimeLeft by viewModel.timeLeft.collectAsState()
    val safeTime = observedTimeLeft.coerceAtLeast(0)

    // 2) Conversión a minutos y segundos para display
    val minutes = (safeTime / 1000) / 60
    val seconds = (safeTime / 1000) % 60

    // 3) Color dinámico: rojo si quedan <10s durante WORKING
    val digitColor = if (safeTime <= 10_000 && currentState == TimerState.State.WORKING)
        RedColor else MaterialTheme.colorScheme.primary

    // Parpadeo cuando queda tiempo crítico en fase de trabajo
    val isCritical = safeTime <= 10_000 && currentState == TimerState.State.WORKING
    val blinkAlpha by animateFloatAsState(
        targetValue   = if (isCritical) 0.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation   = tween(durationMillis = 500),
            repeatMode  = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    // 4) Estados auxiliares para los botones
    val isPaused  = currentState == TimerState.State.PAUSED
    val isRunning = currentState == TimerState.State.WORKING || currentState == TimerState.State.RESTING

    // Color del botón Start según si el temporizador está activo o no
    val startButtonColor =
        if (isRunning) PomodoroResting else MaterialTheme.colorScheme.primary

    // Texto descriptivo del estado
    val estadoTexto = when (currentState) {
        TimerState.State.IDLE     -> ""
        TimerState.State.WORKING  -> "WORK"
        TimerState.State.RESTING  -> "REST"
        TimerState.State.PAUSED   -> "PAUSED"
        TimerState.State.FINISHED -> "FINISHED"
    }

    // 5) Construcción de lista de segmentos (WORKING, RESTING) para la barra
    val totalSegments = totalCycles * 2
    val currentSegment =
        (currentCycle - 1) * 2 + if (currentState == TimerState.State.RESTING) 1 else 0

    val segments = mutableListOf<Pair<Int, TimerState.State>>().apply {
        repeat(totalCycles) {
            add(Pair(workDuration, TimerState.State.WORKING))
            add(Pair(restDuration, TimerState.State.RESTING))
        }
    }

    // Scroll vertical interno para asegurar compatibilidad con pantallas pequeñas
    val scrollState = rememberScrollState()

    // Log de depuración para observar renderizados
    Log.d("PomodoroScreen", "Renderizando: $observedTimeLeft ms - Estado: ${currentState.name}")

    // 6) Contenedor principal con scroll, fondo y padding responsive
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = responsiveWidth(0.08f),
                vertical   = responsiveHeight(0.04f)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 7) Título dinámico que ajusta tamaño según ancho disponible
        BoxWithConstraints {
            val maxContentWidth = maxWidth * 0.9f
            val fontSizeSp      = (maxWidth.value * 0.08f).sp
            val estadoSuperior  =
                if (!isRunning && !isPaused && currentState != TimerState.State.FINISHED)
                    "Push Start ▶️"
                else
                    currentState.name

            Text(
                text      = "State: $estadoSuperior",
                style     = MaterialTheme.typography.titleLarge.copy(fontSize = fontSizeSp),
                color     = MaterialTheme.colorScheme.onBackground,
                modifier  = Modifier
                    .widthIn(max = maxContentWidth)
                    .wrapContentHeight()
                    .padding(bottom = responsiveHeight(0.015f)),
                textAlign = TextAlign.Center,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
        }

        // 8) Cronómetro principal: minutos y segundos con animación de parpadeo
        val screenWidth   = LocalConfiguration.current.screenWidthDp
        val digitFontSize = (screenWidth * 0.6f).sp

        Text(
            text      = "%02d".format(minutes),
            fontSize  = digitFontSize,
            style     = MaterialTheme.typography.displayLarge.copy(color = digitColor),
            modifier  = Modifier.alpha(blinkAlpha)
        )
        Text(
            text      = "%02d".format(seconds),
            fontSize  = digitFontSize,
            style     = MaterialTheme.typography.displayLarge.copy(color = digitColor),
            modifier  = Modifier.alpha(blinkAlpha)
        )

        // 🔢 9) Indicador de ciclo actual con texto resumen
        Text(
            text      = "Cicle $currentCycle of $totalCycles — $estadoTexto",
            style     = MaterialTheme.typography.titleLarge,
            color     = MaterialTheme.colorScheme.onBackground,
            modifier  = Modifier
                .padding(
                    top    = responsiveHeight(0.01f),
                    bottom = responsiveHeight(0.015f)
                ),
            textAlign = TextAlign.Center,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis
        )

        // 📶 10) Barra segmentada que muestra progreso de ciclos
        Spacer(modifier = Modifier.height(responsiveHeight(0.015f)))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(responsiveHeight(0.015f))
                .padding(horizontal = responsiveWidth(0.05f)),
            horizontalArrangement = Arrangement.spacedBy(responsiveWidth(0.008f))
        ) {
            segments.forEachIndexed { index, segment ->
                val (duration, state) = segment
                val isActive    = index == currentSegment
                val isCompleted = index < currentSegment

                // Color dinámico de cada segmento según su estado
                val segmentColor = when {
                    isCompleted                                    -> MaterialTheme.colorScheme.background
                    isActive && currentState == TimerState.State.PAUSED -> RedColor
                    isActive && state == TimerState.State.WORKING  -> MaterialTheme.colorScheme.primary
                    isActive && state == TimerState.State.RESTING  -> Color.White
                    else                                           -> MaterialTheme.colorScheme.surfaceVariant
                }

                // Parpadeo del segmento activo mientras corre
                val blinkAlphaSegment by animateFloatAsState(
                    targetValue   = if (isActive && currentState != TimerState.State.PAUSED
                        && currentState != TimerState.State.FINISHED) 0.3f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation  = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "segmentBlinkAlpha"
                )

                // Peso proporcional al duration para la barra
                val safeWeight = duration.coerceAtLeast(1).toFloat()

                Box(
                    modifier = Modifier
                        .weight(safeWeight)
                        .fillMaxHeight()
                        .background(segmentColor.copy(alpha = blinkAlphaSegment))
                )
            }
        }

        // Función auxiliar interna para escalar fuente según altura de pantalla
        @Composable
        fun responsiveFontSize(multiplier: Float): TextUnit {
            val screenHeight = LocalConfiguration.current.screenHeightDp
            return (screenHeight * multiplier).sp
        }

        // 🎛 11) Botones inferiores de Start, Pause y Reset
        Spacer(modifier = Modifier.height(responsiveHeight(0.04f)))
        val buttonHeight   = responsiveHeight(0.07f)
        val buttonCorner   = responsiveHeight(0.008f)
        val buttonFontSize = responsiveFontSize(0.030f)

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsiveWidth(0.01f)),
            horizontalArrangement = Arrangement.spacedBy(responsiveWidth(0.02f))
        ) {
            // ▶ Start
            Button(
                onClick       = { onStart() },
                modifier      = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                shape         = RoundedCornerShape(buttonCorner),
                colors        = ButtonDefaults.buttonColors(
                    containerColor = startButtonColor,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding= PaddingValues(
                    horizontal = responsiveWidth(0.01f),
                    vertical   = responsiveHeight(0.005f)
                )
            ) {
                Text(
                    "Start",
                    style     = MaterialTheme.typography.labelLarge.copy(fontSize = buttonFontSize),
                    textAlign = TextAlign.Center,
                    maxLines  = 1,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            // ⏸ Pause
            Button(
                onClick       = { onPause() },
                modifier      = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                shape         = RoundedCornerShape(buttonCorner),
                colors        = ButtonDefaults.buttonColors(
                    containerColor = if (isPaused) RedColor else MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding= PaddingValues(
                    horizontal = responsiveWidth(0.01f),
                    vertical   = responsiveHeight(0.005f)
                )
            ) {
                Text(
                    "Pause",
                    style     = MaterialTheme.typography.labelLarge.copy(fontSize = buttonFontSize),
                    textAlign = TextAlign.Center,
                    maxLines  = 1,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            // 🔄 Reset
            Button(
                onClick       = { resetToSetup() },
                modifier      = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                shape         = RoundedCornerShape(buttonCorner),
                colors        = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding= PaddingValues(
                    horizontal = responsiveWidth(0.01f),
                    vertical   = responsiveHeight(0.005f)
                )
            ) {
                Text(
                    "Reset",
                    style     = MaterialTheme.typography.labelLarge.copy(fontSize = buttonFontSize),
                    textAlign = TextAlign.Center,
                    maxLines  = 1,
                    modifier  = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
