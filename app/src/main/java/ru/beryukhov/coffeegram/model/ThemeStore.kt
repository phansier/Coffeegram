package ru.beryukhov.coffeegram.model

import android.content.Context
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.PersistentStore
import ru.beryukhov.coffeegram.store_lib.Storage

class ThemeStore(storage: Storage<ThemeState>): PersistentStore<ThemeIntent, ThemeState>(
    initialState = ThemeState.SYSTEM,
    storage = storage
) {
    override fun handleIntent(intent: ThemeIntent): ThemeState {
        return when (intent) {
            ThemeIntent.SetDarkIntent -> ThemeState.DARK
            ThemeIntent.SetLightIntent -> ThemeState.LIGHT
            ThemeIntent.SetSystemIntent -> ThemeState.SYSTEM
        }
    }
}

sealed class ThemeIntent {
    object SetDarkIntent: ThemeIntent()
    object SetLightIntent: ThemeIntent()
    object SetSystemIntent: ThemeIntent()
}

enum class ThemeState {
    DARK,
    LIGHT,
    SYSTEM
}

fun getThemeStoreStub(context: Context) = ThemeStore(ThemeSharedPrefStorage(context))