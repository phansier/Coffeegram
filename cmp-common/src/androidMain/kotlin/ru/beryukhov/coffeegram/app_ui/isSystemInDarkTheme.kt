package ru.beryukhov.coffeegram.app_ui

import androidx.compose.runtime.Composable
import io.github.alexzhirkevich.cupertino.adaptive.Theme

@Composable
actual fun isSystemInDarkTheme(): Boolean {
    return androidx.compose.foundation.isSystemInDarkTheme()
}

actual fun determineTheme(): Theme = Theme.Material3
