package ru.beryukhov.coffeegram.model

class ThemeStore: Store<ThemeIntent, ThemeState>(
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