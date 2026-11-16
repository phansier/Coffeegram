package ru.beryukhov.coffeegram.wear

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val Brown50 = Color(0xFFEFEBE9)
private val Brown200 = Color(0xFFBCAAA4)
private val Brown500 = Color(0xFF795548)
private val Brown700 = Color(0xFF5D4037)
private val Teal200 = Color(0xFF03DAC5)

private val CoffeegramColorScheme = ColorScheme(
    primary = Brown500,
    primaryDim = Brown700,
    primaryContainer = Brown700,
    onPrimary = Brown50,
    onPrimaryContainer = Brown50,
    secondary = Teal200,
    onSecondary = Color.Black,
    tertiary = Brown200,
    onTertiary = Color.Black,
)

@Composable
fun CoffeegramTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CoffeegramColorScheme, content = content)
}
