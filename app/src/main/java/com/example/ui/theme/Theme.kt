package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VantaDarkColorScheme = darkColorScheme(
    primary = VantaAccentLight,
    onPrimary = VantaNavyDark,
    primaryContainer = VantaNavySurface,
    onPrimaryContainer = VantaSilverLight,
    secondary = VantaSilver,
    onSecondary = VantaNavyDark,
    tertiary = Color(0xFF6EE7B7),
    background = VantaNavyDark,
    onBackground = Color(0xFFF1F5F9),
    surface = VantaNavy,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = VantaNavySurface,
    onSurfaceVariant = VantaSilver,
    outline = VantaNavyElevated,
    outlineVariant = Color(0xFF334155)
)

private val VantaLightColorScheme = lightColorScheme(
    primary = VantaNavy,
    onPrimary = Color.White,
    primaryContainer = VantaSilverLight,
    onPrimaryContainer = VantaNavy,
    secondary = VantaAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF1E3A8A),
    tertiary = Color(0xFF0D9488),
    background = VantaBackgroundLight,
    onBackground = VantaTextPrimary,
    surface = VantaSurfaceLight,
    onSurface = VantaTextPrimary,
    surfaceVariant = Color(0xFFEDF2F7),
    onSurfaceVariant = VantaTextSecondary,
    outline = VantaCardBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun VantaDigitalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded navy & silver palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> VantaDarkColorScheme
        else -> VantaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VantaDigitalTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
