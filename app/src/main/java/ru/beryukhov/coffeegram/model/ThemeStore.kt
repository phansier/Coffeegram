package ru.beryukhov.coffeegram.model

import android.content.Context
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.PersistentStore
import ru.beryukhov.coffeegram.store_lib.Storage

class ThemeStore(storage: Storage<ThemeState>) : PersistentStore<ThemeIntent, ThemeState>(
    initialState = ThemeStateDefault,
    storage = storage
) {
    override fun ThemeState.handleIntent(intent: ThemeIntent): ThemeState {
        return when (intent) {
            ThemeIntent.SetDarkIntent -> copy(useDarkTheme = DarkThemeState.DARK)
            ThemeIntent.SetLightIntent -> copy(useDarkTheme = DarkThemeState.LIGHT)
            ThemeIntent.SetSystemIntent -> copy(useDarkTheme = DarkThemeState.SYSTEM)
            ThemeIntent.SetDynamicIntent -> copy(isDynamic = true)
            ThemeIntent.UnSetDynamicIntent -> copy(isDynamic = false)
            ThemeIntent.SetSummerIntent -> copy(isSummer = true)
            ThemeIntent.UnSetSummerIntent -> copy(isSummer = false)
        }
    }
}

sealed interface ThemeIntent {
    data object SetDarkIntent : ThemeIntent
    data object SetLightIntent : ThemeIntent
    data object SetSystemIntent : ThemeIntent
    data object SetDynamicIntent : ThemeIntent
    data object UnSetDynamicIntent : ThemeIntent
    data object SetSummerIntent : ThemeIntent
    data object UnSetSummerIntent : ThemeIntent
}

data class ThemeState(val useDarkTheme: DarkThemeState, val isDynamic: Boolean, val isSummer: Boolean)

val ThemeStateDefault get() = ThemeState(
    useDarkTheme = DarkThemeState.SYSTEM,
    isDynamic = true,
    isSummer = false
)

enum class DarkThemeState {
    DARK,
    LIGHT,
    SYSTEM
}

fun getThemeStoreStub(context: Context) = ThemeStore(ThemeSharedPrefStorage(context))
