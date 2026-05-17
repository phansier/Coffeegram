package ru.beryukhov.coffeegram.app_ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual fun isCupertinoDefault(): Boolean = false

@Composable
actual fun dynamicColorSchemeOrNull(darkTheme: Boolean): ColorScheme? = null
