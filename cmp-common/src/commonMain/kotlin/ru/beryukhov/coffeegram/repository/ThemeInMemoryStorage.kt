package ru.beryukhov.coffeegram.repository

import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.store_lib.Storage

class ThemeInMemoryStorage : Storage<ThemeState> {
    private var themeState: ThemeState? = null

    override suspend fun getState(): ThemeState? = themeState
    override suspend fun saveState(state: ThemeState) {
        themeState = state
    }
}
