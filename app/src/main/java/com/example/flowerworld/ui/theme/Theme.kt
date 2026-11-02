package com.example.flowerworld.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Sage,
    onPrimary = BrunswickGreen,
    primaryContainer = HunterGreen,
    onPrimaryContainer = Timberwolf,
    secondary = FernGreen,
    onSecondary = Timberwolf,
    tertiary = Timberwolf,
    onTertiary = BrunswickGreen,
    background = BrunswickGreen,
    onBackground = Timberwolf,
    surface = BrunswickGreen,
    onSurface = Timberwolf
)

private val LightColorScheme = lightColorScheme(
    primary = HunterGreen,
    onPrimary = Timberwolf,
    primaryContainer = Sage,
    onPrimaryContainer = BrunswickGreen,
    secondary = FernGreen,
    onSecondary = Timberwolf,
    tertiary = BrunswickGreen,
    onTertiary = Timberwolf,
    background = Timberwolf,
    onBackground = TextPrimary,
    surface = Timberwolf,
    onSurface = TextPrimary
)

@Composable
fun FlowerWorldTheme(
    darkTheme: Boolean = false,
    // Dynamic color (Android 12+) is off so the brand palette is always used
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}