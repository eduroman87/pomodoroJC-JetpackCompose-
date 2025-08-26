package com.eduroman87pomodorojc.ui.screens.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eduroman87.pomodorojc.ui.screens.SetupScreen
import com.eduroman87.pomodorojc.ui.theme.AppTheme

@Preview(
    name = "SetupScreen - Móvil",
    showBackground = true,
    widthDp = 360,
    heightDp = 640,
    apiLevel = 33
)
@Composable
fun PreviewSetupScreenMobile() {
    AppTheme {
            SetupScreen(
            onConfirm = { c, s, r ->
                println("Ciclos: $c - Estudio: $s - Descanso: $r")
            }
        )
    }
}
