package ru.beryukhov.coffeegram.repository

import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStorage

class ThemeInMemoryStorage : ThemeStorage {
    private var themeState: ThemeState? = null

    override suspend fun getState(): ThemeState? = themeState
    override suspend fun saveState(state: ThemeState) {
        themeState = state
    }
}
