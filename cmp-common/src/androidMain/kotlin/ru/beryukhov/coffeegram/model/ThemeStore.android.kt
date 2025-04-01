package ru.beryukhov.coffeegram.model

import android.os.Build

actual val ThemeStateDefault = ThemeState(
    useDarkTheme = DarkThemeState.SYSTEM,
    isCupertino = false,
    isDynamic = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) false else null,
    isSummer = false
)
