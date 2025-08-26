package com.eduroman87.pomodorojc.ui.screens

// Importaciones necesarias para animaciones, diseño responsive y estilos visuales
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.eduroman87.pomodorojc.ui.theme.AppTheme
import com.eduroman87.pomodorojc.ui.theme.AppTypography
import com.eduroman87pomodorojc.utils.responsiveFontSize
import com.eduroman87pomodorojc.utils.responsiveHeight
import com.eduroman87pomodorojc.utils.responsiveWidth

/**
 * Pantalla de configuración inicial de la sesión Pomodoro.
 * Permite al usuario definir número de ciclos, duración de trabajo y descanso.
 * Valida los campos y aplica animaciones visuales para indicar errores.
 */
@Composable
fun SetupScreen(
    onConfirm: (Int, Long, Long) -> Unit // Callback que se ejecuta al confirmar los datos
) {
    // Estados para los campos de entrada
    var cicles       by remember { mutableStateOf("") }
    var workMinutes  by remember { mutableStateOf("") }
    var workSeconds  by remember { mutableStateOf("") }
    var restMinutes  by remember { mutableStateOf("") }
    var restSeconds  by remember { mutableStateOf("") }

    // Mapa de errores por campo, usado para atenuar inputs con error
    val fieldErrors = remember {
        mutableStateMapOf<String, Boolean>().apply {
            listOf(
                "cicles", "workHours", "workMinutes", "workSeconds",
                "restHours", "restMinutes", "restSeconds"
            ).forEach { this[it] = false }
        }
    }

    // Estados auxiliares: control de animación de errores y normalización
    var showError    by remember { mutableStateOf(false) }
    var normalized   by remember { mutableStateOf(false) }
    var confirmLabel by remember { mutableStateOf("FILL IN THE DATA") }

    // Animación para atenuar todos los campos en caso de error general
    val errorAlpha by animateFloatAsState(
        targetValue   = if (showError) 0.3f else 1f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label         = "errorAlpha"
    )

    // Dimensiones responsive para inputs, textos y botón
    val inputWidth         = responsiveWidth(0.24f)
    val inputHeight        = responsiveHeight(0.095f)
    val inputPadding       = responsiveWidth(0.008f)
    val titleFontSize      = responsiveFontSize(0.09f)
    val labelFontSize      = responsiveFontSize(0.060f)
    val labelFontSizeColumn= responsiveFontSize(0.077f)
    val buttonHeight       = responsiveHeight(0.07f)
    val buttonCorner       = responsiveHeight(0.008f)

    /**
     * Función auxiliar que aplica animación de error a un campo específico.
     * Se usa para atenuar inputs individualmente cuando tienen error.
     */
    @Composable
    fun animatedErrorAlpha(fieldKey: String): Float {
        val hasError = fieldErrors[fieldKey] ?: false
        return animateFloatAsState(
            targetValue   = if (hasError) 0.3f else 1f,
            animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
            label         = "errorAlpha_$fieldKey"
        ).value
    }

    // Contenedor principal de la UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = responsiveWidth(0.06f),
                vertical   = responsiveHeight(0.04f)
            ),
        verticalArrangement   = Arrangement.SpaceEvenly,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        // Título principal de la pantalla
        Text(
            text      = "Setup your session 🍅",
            style     = AppTypography.labelLarge.copy(fontSize = titleFontSize),
            color     = MaterialTheme.colorScheme.primary,
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // 🔴 Fila de entrada para "Cicles" (número de ciclos Pomodoro)
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(bottom = responsiveHeight(0.006f)),
            horizontalArrangement = Arrangement.spacedBy(inputPadding),
            verticalAlignment   = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Espacio en blanco para alinear
            Spacer(modifier = Modifier.weight(1f))

            // Etiqueta "Cicles"
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .height(inputHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text      = "Cicles",
                    style     = AppTypography.labelLarge.copy(fontSize = labelFontSizeColumn),
                    color     = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    maxLines  = 1
                )
            }

            // Campo de texto para ingresar número de ciclos
            OutlinedTextField(
                value         = cicles,
                onValueChange = {
                    cicles     = it
                    normalized = false
                },
                isError       = fieldErrors["cicles"] == true,
                modifier      = Modifier
                    .weight(1f)
                    .height(inputHeight)
                    .alpha(animatedErrorAlpha("cicles")),
                singleLine    = true
            )
        }

        // 🟦 Encabezado con "Minutes" / "Seconds" para trabajo y descanso
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(bottom = responsiveHeight(0.006f)),
            horizontalArrangement = Arrangement.spacedBy(inputPadding),
            verticalAlignment   = Alignment.CenterVertically
        ) {
            // Columna vacía para alinear con la etiqueta de la izquierda
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .height(inputHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text      = " ", // espacio invisible para alineación
                    style     = AppTypography.labelLarge.copy(fontSize = labelFontSize),
                    textAlign = TextAlign.Start,
                    maxLines  = 1
                )
            }

            // Etiquetas de columna: Minutos y Segundos
            listOf("Minutes", "Seconds").forEach { label ->
                Box(
                    modifier         = Modifier
                        .weight(1f)
                        .height(inputHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text      = label,
                        style     = AppTypography.labelLarge.copy(fontSize = labelFontSizeColumn),
                        color     = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        maxLines  = 1
                    )
                }
            }
        }

        // 🟢 Composable interno para renderizar filas tituladas (Work / Rest)
        @Composable
        fun labeledRow(
            title     : String,
            fields    : List<String>,
            onChanges : List<(String) -> Unit>
        ) {
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(inputPadding),
                verticalAlignment   = Alignment.CenterVertically
            ) {
                // Título de la fila (ej. "Work" o "Rest")
                Box(
                    modifier         = Modifier
                        .weight(1f)
                        .height(inputHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = title,
                        style     = AppTypography.labelLarge.copy(fontSize = labelFontSizeColumn),
                        color     = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Start,
                        maxLines  = 1
                    )
                }

                // Campos de entrada alineados con el encabezado
                fields.zip(onChanges).forEach { (value, onChange) ->
                    OutlinedTextField(
                        value         = value,
                        onValueChange = onChange,
                        modifier      = Modifier
                            .weight(1f)
                            .height(inputHeight)
                            .alpha(errorAlpha),
                        singleLine    = true
                    )
                }
            }
        }

        // 🟨 Filas de entrada de Work y Rest usando el Composable reutilizable
        labeledRow(
            title     = "Work",
            fields    = listOf(workMinutes, workSeconds),
            onChanges = listOf({ workMinutes = it }, { workSeconds = it })
        )
        labeledRow(
            title     = "Rest",
            fields    = listOf(restMinutes, restSeconds),
            onChanges = listOf({ restMinutes = it }, { restSeconds = it })
        )

        // Evaluación rápida para habilitar cambio de estado del botón
        val atLeastOneWork = listOf(workMinutes, workSeconds).any  { it.trim().isNotEmpty() }
        val atLeastOneRest = listOf(restMinutes, restSeconds).any { it.trim().isNotEmpty() }
        val cValid         = cicles.trim().toIntOrNull() != null
        val canSwitchToReady = cValid && atLeastOneWork && atLeastOneRest

        // Actualiza la etiqueta del botón cuando cambia el estado de validación
        LaunchedEffect(canSwitchToReady, normalized) {
            confirmLabel = when {
                !canSwitchToReady            -> "FILL IN THE DATA"
                canSwitchToReady && !normalized -> "READY?"
                else                          -> "CONFIRM SESSION"
            }
        }

        // Botón que gestiona normalización, validación final y callback onConfirm
        Button(
            onClick = {
                // 1) NORMALIZAR: cuando estamos en "READY?"
                if (confirmLabel == "READY?") {
                    val wM = workMinutes.trim().toIntOrNull() ?: 0
                    val wS = workSeconds.trim().toIntOrNull() ?: 0
                    val rM = restMinutes.trim().toIntOrNull() ?: 0
                    val rS = restSeconds.trim().toIntOrNull() ?: 0

                    val totalWorkSeconds = wM * 60 + wS
                    val totalRestSeconds = rM * 60 + rS

                    // Reescribe campos con valores normalizados
                    workMinutes = ((totalWorkSeconds % 3600) / 60).toString()
                    workSeconds = (totalWorkSeconds % 60).toString()
                    restMinutes = ((totalRestSeconds % 3600) / 60).toString()
                    restSeconds = (totalRestSeconds % 60).toString()
                    normalized = true
                    return@Button
                }

                // 2) VALIDAR Y CONFIRMAR: cuando estamos en "CONFIRM SESSION"
                if (confirmLabel == "CONFIRM SESSION") {
                    val c  = cicles.trim().toIntOrNull()
                    val wM = workMinutes.trim().toIntOrNull() ?: 0
                    val wS = workSeconds.trim().toIntOrNull() ?: 0
                    val rM = restMinutes.trim().toIntOrNull() ?: 0
                    val rS = restSeconds.trim().toIntOrNull() ?: 0

                    // Se suman 3600 para forzar >0 en validación, según lógica actual
                    val totalWorkSeconds = 3600 + wM * 60 + wS
                    val totalRestSeconds = 3600 + rM * 60 + rS

                    // 🔁 Limpia errores previos
                    fieldErrors.keys.forEach { fieldErrors[it] = false }

                    // ✅ Validaciones individuales
                    val workValid = totalWorkSeconds > 0
                    val restValid = totalRestSeconds > 0
                    if (c == null)              fieldErrors["cicles"]    = true
                    if (!workValid) {
                        fieldErrors["workHours"]   = true
                        fieldErrors["workMinutes"] = true
                        fieldErrors["workSeconds"] = true
                    }
                    if (!restValid) {
                        fieldErrors["restHours"]   = true
                        fieldErrors["restMinutes"] = true
                        fieldErrors["restSeconds"] = true
                    }

                    val canProceed = c != null && workValid && restValid
                    if (!canProceed) return@Button

                    showError = false
                    val totalWorkMillis = (wM * 60 + wS) * 1000L
                    val totalRestMillis = (rM * 60 + rS) * 1000L

                    // Llamada final con los datos corregidos en milisegundos
                    onConfirm(c!!, totalWorkMillis, totalRestMillis)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            shape = RoundedCornerShape(buttonCorner),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text      = confirmLabel,
                style     = AppTypography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines  = 1,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Vista previa de la pantalla SetupScreen en entorno móvil.
 * Útil para validar diseño y comportamiento sin ejecutar la app completa.
 */
@Preview(
    name       = "SetupScreen - Móvil",
    showBackground = true,
    widthDp    = 360,
    heightDp   = 640,
    apiLevel   = 33
)
@Composable
fun PreviewSetupScreenMobile() {
    AppTheme {
        SetupScreen { c, workMs, restMs ->
            println("Ciclos: $c, trabajo: $workMs ms, descanso: $restMs ms")
        }
    }
}
