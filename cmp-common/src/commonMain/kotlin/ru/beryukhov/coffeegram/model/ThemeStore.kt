package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.coffeegram.store_lib.StoreImpl

class ThemeStore(storage: Storage<ThemeState>) : StoreImpl<ThemeIntent, ThemeState>(
    initialState = ThemeStateDefault,
    storage = storage
) {
    override fun ThemeState.handleIntent(intent: ThemeIntent): ThemeState {
        return when (intent) {
            ThemeIntent.SetDarkIntent -> copy(useDarkTheme = DarkThemeState.DARK)
            ThemeIntent.SetLightIntent -> copy(useDarkTheme = DarkThemeState.LIGHT)
            ThemeIntent.SetSystemIntent -> copy(useDarkTheme = DarkThemeState.SYSTEM)
            is ThemeIntent.SetCupertinoIntent -> copy(isCupertino = isCupertino?.let { intent.enabled })
            is ThemeIntent.SetDynamicIntent -> copy(isDynamic = isDynamic?.let { intent.enabled })
            is ThemeIntent.SetSummerIntent -> copy(isSummer = isSummer?.let { intent.enabled })
        }
    }
}

sealed interface ThemeIntent {
    object SetDarkIntent : ThemeIntent
    object SetLightIntent : ThemeIntent
    object SetSystemIntent : ThemeIntent
    data class SetCupertinoIntent(val enabled: Boolean) : ThemeIntent
    data class SetDynamicIntent(val enabled: Boolean) : ThemeIntent
    data class SetSummerIntent(val enabled: Boolean) : ThemeIntent
}

data class ThemeState(
    val useDarkTheme: DarkThemeState,
    val isCupertino: Boolean?,
    val isDynamic: Boolean?,
    val isSummer: Boolean?,
)

// if val is null - this config is unavailable on this platform
// otherwise - this config sets default value
expect val ThemeStateDefault: ThemeState

enum class DarkThemeState {
    DARK,
    LIGHT,
    SYSTEM
}
