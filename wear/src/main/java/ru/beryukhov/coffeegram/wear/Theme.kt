package ru.beryukhov.coffeegram.wear

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val CoffeegramColorScheme = ColorScheme(
    primary = Color(BrandColors.BROWN_500),
    primaryDim = Color(BrandColors.BROWN_700),
    primaryContainer = Color(BrandColors.BROWN_700),
    onPrimary = Color(BrandColors.BROWN_50),
    onPrimaryContainer = Color(BrandColors.BROWN_50),
    secondary = Color(BrandColors.TEAL_200),
    onSecondary = Color(BrandColors.BLACK),
    tertiary = Color(BrandColors.BROWN_200),
    onTertiary = Color(BrandColors.BLACK),
)

@Composable
fun CoffeegramTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CoffeegramColorScheme, content = content)
}
