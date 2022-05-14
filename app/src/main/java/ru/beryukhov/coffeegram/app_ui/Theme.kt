package ru.beryukhov.coffeegram.app_ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
private fun darkColorPalette(): ColorScheme = darkColorScheme(
    primary = brown200,
    inversePrimary = brown700,
    secondary = teal200,
    surface = brown500,
)

@Composable
private fun lightColorPalette(): ColorScheme = lightColorScheme(
    primary = brown500,
    inversePrimary = brown700,
    secondary = teal200,
    surface = brown200,
)

@Composable
fun CoffeegramTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        darkColorPalette()
    } else {
        lightColorPalette()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
