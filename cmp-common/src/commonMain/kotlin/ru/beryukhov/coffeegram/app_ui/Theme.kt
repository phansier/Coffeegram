package ru.beryukhov.coffeegram.app_ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStateDefault

private val DarkColorPalette = darkColors(
    primary = brown200,
    primaryVariant = brown700,
    secondary = teal200
)

private val LightColorPalette = lightColors(
    primary = brown500,
    primaryVariant = brown700,
    secondary = teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CoffeegramTheme(themeState: ThemeState = ThemeStateDefault, content: @Composable () -> Unit) {
    val darkTheme = when (themeState.useDarkTheme) {
        DarkThemeState.DARK -> true
        DarkThemeState.LIGHT -> false
        DarkThemeState.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
expect fun isSystemInDarkTheme(): Boolean
