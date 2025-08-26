package com.eduroman87.pomodorojc.ui.components

// Importaciones necesarias para composición de filas, inputs y estilos responsive
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import com.eduroman87.pomodorojc.ui.theme.AppTypography
import com.eduroman87.pomodorojc.ui.theme.RedColor
import com.eduroman87pomodorojc.utils.responsiveWidth

/**
 * Fila de configuración reutilizable que muestra una etiqueta y un campo de texto.
 * Incluye manejo de estado de error con animación de opacidad.
 *
 * @param label          Texto descriptivo que aparece a la izquierda.
 * @param value          Valor actual que se muestra dentro del OutlinedTextField.
 * @param onValueChange  Lambda que se invoca cuando el texto del input cambia.
 * @param showError      Indica si el campo está en estado de error (borde rojo).
 * @param errorAlpha     Factor de opacidad para animación de campos con error.
 */
@Composable
fun ConfigInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showError: Boolean,
    errorAlpha: Float
) {
    // Contenedor horizontal: etiqueta a la izquierda y campo de texto a la derecha
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement= Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically
    ) {
        // Texto de etiqueta
        Text(
            text  = label,
            style = AppTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Campo de texto con borde y manejo de error
        OutlinedTextField(
            value            = value,
            onValueChange    = onValueChange,
            singleLine       = true,
            isError          = showError,                                   // Muestra borde rojo si hay error
            modifier         = Modifier
                .width(responsiveWidth(0.25f))                            // Ancho responsive basado en porcentaje de pantalla
                .alpha(errorAlpha),                                       // Opacidad animada en caso de error
            colors           = OutlinedTextFieldDefaults.colors(
                errorBorderColor = RedColor                               // Color del borde cuando isError == true
            ),
            textStyle        = AppTypography.bodyMedium                    // Estilo de texto consistente con la app
        )
    }
}
