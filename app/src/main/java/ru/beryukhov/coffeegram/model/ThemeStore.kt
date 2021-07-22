package ru.beryukhov.coffeegram.model

import android.content.Context
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage
import ru.beryukhov.coffeegram.store_lib.PersistentStore

class ThemeStore(context: Context): PersistentStore<ThemeIntent, ThemeState>(
    initialState = ThemeState.SYSTEM,
    storage = ThemeSharedPrefStorage(context = context)
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