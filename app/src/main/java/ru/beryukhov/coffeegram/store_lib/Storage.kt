package ru.beryukhov.coffeegram.store_lib

interface Storage<State: Any> {
    suspend fun getState(): State?
    suspend fun saveState(state: State)
}