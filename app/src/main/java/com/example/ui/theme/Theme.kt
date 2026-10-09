package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = DarkNavyBase,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldLight,
    secondary = CyanSecondary,
    onSecondary = DarkNavyBase,
    secondaryContainer = CyanDark,
    onSecondaryContainer = CyanLight,
    tertiary = OrangeFlame,
    onTertiary = DarkNavyBase,
    background = DarkNavyBase,
    onBackground = TextPrimaryDark,
    surface = DarkNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = LightSurface,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = DarkNavyBase,
    secondary = CyanDark,
    onSecondary = LightSurface,
    secondaryContainer = CyanLight,
    onSecondaryContainer = DarkNavyBase,
    tertiary = OrangeFlame,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun FitPulseTheme(
    darkTheme: Boolean = true, // Default to sleek athletic dark theme
    dynamicColor: Boolean = false, // Keep branded cohesive palette
    content: @Composable () -> Unit,
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

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = FitPulseTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
