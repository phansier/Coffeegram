package ru.beryukhov.coffeegram.model

import android.content.Context
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
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
            ThemeIntent.SetDynamicIntent -> state.value.copy(isDynamic = true, dynamicSnackbarShow = true)
            ThemeIntent.UnSetDynamicIntent -> state.value.copy(isDynamic = false)
            ThemeIntent.DismissDynamicSnackbar -> state.value.copy(dynamicSnackbarShow = false)
        }
    }
}

sealed interface ThemeIntent {
    object SetDarkIntent : ThemeIntent
    object SetLightIntent : ThemeIntent
    object SetSystemIntent : ThemeIntent
    object SetDynamicIntent : ThemeIntent
    object UnSetDynamicIntent : ThemeIntent
    object DismissDynamicSnackbar : ThemeIntent
}

data class ThemeState(val useDarkTheme: DarkThemeState, val isDynamic: Boolean, val dynamicSnackbarShow: Boolean)

val ThemeStateDefault get() = ThemeState(
    useDarkTheme = DarkThemeState.SYSTEM,
    isDynamic = true,
    dynamicSnackbarShow = false
)

enum class DarkThemeState {
    DARK,
    LIGHT,
    SYSTEM
}

fun getThemeStoreStub(context: Context) = ThemeStore(ThemeSharedPrefStorage(context))
