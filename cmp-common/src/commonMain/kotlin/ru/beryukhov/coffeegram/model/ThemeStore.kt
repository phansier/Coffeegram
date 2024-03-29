package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.app_ui.isCupertinoDefault
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
            ThemeIntent.SetCupertinoIntent -> state.value.copy(isCupertino = true)
            ThemeIntent.UnSetCupertinoIntent -> state.value.copy(isCupertino = false)
        }
    }
}

sealed interface ThemeIntent {
    object SetDarkIntent : ThemeIntent
    object SetLightIntent : ThemeIntent
    object SetSystemIntent : ThemeIntent
    object SetCupertinoIntent : ThemeIntent
    object UnSetCupertinoIntent : ThemeIntent
}

data class ThemeState(val useDarkTheme: DarkThemeState, val isCupertino: Boolean)

val ThemeStateDefault get() = ThemeState(DarkThemeState.SYSTEM, isCupertino = isCupertinoDefault())

enum class DarkThemeState {
    DARK,
    LIGHT,
    SYSTEM
}
