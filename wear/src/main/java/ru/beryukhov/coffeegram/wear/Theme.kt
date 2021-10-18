package ru.beryukhov.coffeegram.wear

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.MaterialTheme.shapes
import androidx.wear.compose.material.MaterialTheme.typography

val brown200 = Color(0xFFBCAAA4)
val brown500 = Color(0xFF795548)
val brown700 = Color(0xFF5D4037)
val teal200 = Color(0xFF03DAC5)//R.color.teal_200)

private val DarkColorPalette = Colors(
    primary = brown200,
    primaryVariant = brown700,
    secondary = teal200
)

private val LightColorPalette = Colors(
    primary = brown500,
    primaryVariant = brown700,
    secondary = teal200
)


//Todo reuse with main app
@Composable
fun CoffeegramTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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
