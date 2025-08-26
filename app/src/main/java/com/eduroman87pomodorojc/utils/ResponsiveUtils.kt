package com.eduroman87pomodorojc.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun responsiveWidth(fraction: Float): Dp {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return (screenWidthDp * fraction).dp
}

@Composable
fun responsiveHeight(fraction: Float): Dp {
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    return (screenHeightDp * fraction).dp
}
@Composable
fun responsiveFontSize(factor: Float): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth * factor).sp
}
