package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VermilionDarkPrimary,
    onPrimary = VermilionDarkOnPrimary,
    primaryContainer = VermilionDarkPrimaryContainer,
    onPrimaryContainer = VermilionDarkOnPrimaryContainer,
    secondary = AmberDarkSecondary,
    onSecondary = AmberDarkOnSecondary,
    secondaryContainer = AmberDarkSecondaryContainer,
    onSecondaryContainer = AmberDarkOnSecondaryContainer,
    tertiary = JadeDarkTertiary,
    onTertiary = JadeDarkOnTertiary,
    tertiaryContainer = JadeDarkTertiaryContainer,
    onTertiaryContainer = JadeDarkOnTertiaryContainer,
    background = InkDarkBackground,
    onBackground = InkDarkOnBackground,
    surface = InkDarkSurface,
    onSurface = InkDarkOnSurface,
    surfaceVariant = InkDarkSurfaceVariant,
    onSurfaceVariant = InkDarkOnSurfaceVariant,
    outline = InkDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = VermilionPrimary,
    onPrimary = VermilionOnPrimary,
    primaryContainer = VermilionPrimaryContainer,
    onPrimaryContainer = VermilionOnPrimaryContainer,
    secondary = AmberSecondary,
    onSecondary = AmberOnSecondary,
    secondaryContainer = AmberSecondaryContainer,
    onSecondaryContainer = AmberOnSecondaryContainer,
    tertiary = JadeTertiary,
    onTertiary = JadeOnTertiary,
    tertiaryContainer = JadeTertiaryContainer,
    onTertiaryContainer = JadeOnTertiaryContainer,
    background = ParchmentBackground,
    onBackground = ParchmentOnBackground,
    surface = ParchmentSurface,
    onSurface = ParchmentOnSurface,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = ParchmentOnSurfaceVariant,
    outline = ParchmentOutline
)

@Composable
fun MyApplicationTheme(
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
