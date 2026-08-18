package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BofARed,
    onPrimary = Color.White,
    primaryContainer = BofADeepRed,
    onPrimaryContainer = Color.White,
    secondary = BofANavy,
    onSecondary = Color.White,
    tertiary = PreferredGold,
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = CardBackgroundDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    error = NegativeRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BofARed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE8EB),
    onPrimaryContainer = BofARed,
    secondary = BofANavy,
    onSecondary = Color.White,
    tertiary = PreferredGold,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardBackgroundLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = NegativeRed,
    onError = Color.White
)

@Composable
fun ApexFinancialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
