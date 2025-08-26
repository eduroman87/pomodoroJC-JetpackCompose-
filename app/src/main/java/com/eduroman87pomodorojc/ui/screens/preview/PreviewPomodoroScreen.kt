package com.eduroman87.pomodorojc.ui.screens.preview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.eduroman87.pomodorojc.ui.screens.PomodoroScreen
import com.eduroman87.pomodorojc.domain.TimerState
import com.eduroman87.pomodorojc.ui.theme.AppTheme // reemplázalo si tu tema tiene otro nombre
import com.eduroman87.pomodorojc.viewmodel.PomodoroViewModel

@Preview(
    name = "Pomodoro - Móvil",
    showBackground = true,
    widthDp = 360,
    heightDp = 640,
    apiLevel = 33
)
@Composable
fun PreviewPomodoroMobile() {
    AppTheme {
        val dummyViewModel = remember { PomodoroViewModel() }
        PomodoroScreen(
            //timeLeft = 25 * 60_000L,
            currentState = TimerState.State.IDLE,
            currentCycle = 1,
            totalCycles = 4,
            workDuration = 25,
            restDuration = 5,
            onStart = {},
            onPause = {},
            resetToSetup = {},
            viewModel = dummyViewModel
        )
    }
}

@Preview(
    name = "Pomodoro - Tablet",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    apiLevel = 33
)
@Composable
fun PreviewPomodoroTablet() {
    AppTheme {
        val dummyViewModel = remember { PomodoroViewModel() }
        PomodoroScreen(
            //timeLeft = 5 * 60_000L,
            currentState = TimerState.State.WORKING,
            currentCycle = 3,
            totalCycles = 4,
            workDuration = 25,
            restDuration = 5,
            onStart = {},
            onPause = {},
            resetToSetup = {},
            viewModel = dummyViewModel
        )
    }
}

@Preview(
    name = "Pomodoro - Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = 33
)
@Composable
fun PreviewPomodoroDark() {
    AppTheme {
        val dummyViewModel = remember { PomodoroViewModel() }
        PomodoroScreen(
           // timeLeft = 0L,
            currentState = TimerState.State.FINISHED,
            currentCycle = 4,
            totalCycles = 4,
            workDuration = 25,
            restDuration = 5,
            onStart = {},
            onPause = {},
            resetToSetup = {},
            viewModel = dummyViewModel
        )
    }
}
