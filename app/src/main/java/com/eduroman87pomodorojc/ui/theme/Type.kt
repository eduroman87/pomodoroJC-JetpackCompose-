package com.eduroman87.pomodorojc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font


import androidx.compose.ui.unit.sp
import com.eduroman87pomodorojc.R

val SegmentFont = FontFamily(
    Font(R.font.ds_digital)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 220.sp,
        fontFamily = SegmentFont,
        fontWeight = FontWeight.Normal
    ),
    titleLarge = TextStyle(
        fontSize = 32.sp,
        fontFamily = SegmentFont,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontSize = 18.sp,
        fontFamily = SegmentFont
    ),
    labelLarge = TextStyle(fontSize = 30.sp,
    fontFamily = SegmentFont,
    fontWeight = FontWeight.Normal
)
)
