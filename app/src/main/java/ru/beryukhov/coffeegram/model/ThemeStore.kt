package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class ThemeStore: InMemoryStore<ThemeIntent, ThemeState>(
    initialState = ThemeState.SYSTEM
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