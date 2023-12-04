package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.store_lib.PersistentStore
import ru.beryukhov.coffeegram.store_lib.Storage

class ThemeStore(storage: Storage<ThemeState>) : PersistentStore<ThemeIntent, ThemeState>(
    initialState = ThemeStateDefault,
    storage = storage
) {
    override fun handleIntent(intent: ThemeIntent): ThemeState {
        return when (intent) {
            ThemeIntent.SetDarkIntent -> state.value.copy(useDarkTheme = DarkThemeState.DARK)
            ThemeIntent.SetLightIntent -> state.value.copy(useDarkTheme = DarkThemeState.LIGHT)
            ThemeIntent.SetSystemIntent -> state.value.copy(useDarkTheme = DarkThemeState.SYSTEM)
            ThemeIntent.SetDynamicIntent -> state.value.copy(isDynamic = true)
            ThemeIntent.UnSetDynamicIntent -> state.value.copy(isDynamic = false)
        }
    }
}

sealed interface ThemeIntent {
    object SetDarkIntent : ThemeIntent
    object SetLightIntent : ThemeIntent
    object SetSystemIntent : ThemeIntent
    object SetDynamicIntent : ThemeIntent
    object UnSetDynamicIntent : ThemeIntent
}

data class ThemeState(val useDarkTheme: DarkThemeState, val isDynamic: Boolean)

val ThemeStateDefault get() = ThemeState(DarkThemeState.SYSTEM, true)

enum class DarkThemeState {
    DARK,
    LIGHT,
    SYSTEM
}
