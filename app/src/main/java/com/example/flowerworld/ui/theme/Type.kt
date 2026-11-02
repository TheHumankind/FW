package com.example.flowerworld.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private val Base = Typography()

val Typography = Typography(
    displayLarge = Base.displayLarge.withAppFont(),
    displayMedium = Base.displayMedium.withAppFont(),
    displaySmall = Base.displaySmall.withAppFont(),
    headlineLarge = Base.headlineLarge.withAppFont(),
    headlineMedium = Base.headlineMedium.withAppFont(),
    headlineSmall = Base.headlineSmall.withAppFont(),
    titleLarge = Base.titleLarge.withAppFont(),
    titleMedium = Base.titleMedium.withAppFont(),
    titleSmall = Base.titleSmall.withAppFont(),
    bodyLarge = Base.bodyLarge.withAppFont(),
    bodyMedium = Base.bodyMedium.withAppFont(),
    bodySmall = Base.bodySmall.withAppFont(),
    // Текст кнопок — жирный
    labelLarge = Base.labelLarge.withAppFont().copy(fontWeight = FontWeight.Bold),
    labelMedium = Base.labelMedium.withAppFont().copy(fontWeight = FontWeight.Bold),
    labelSmall = Base.labelSmall.withAppFont()
)

private fun TextStyle.withAppFont() = copy(fontFamily = AppFontFamily)
