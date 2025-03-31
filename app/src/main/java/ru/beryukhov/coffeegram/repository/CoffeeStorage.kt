package ru.beryukhov.coffeegram.repository

import repository.CoffeeRepository
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.store_lib.Storage

class CoffeeStorage(private val repository: CoffeeRepository) :
    Storage<DaysCoffeesState> {
    override suspend fun getState(): DaysCoffeesState {
        return repository.getAll().toState()
    }

    override suspend fun saveState(state: DaysCoffeesState) {
        repository.createOrUpdate(state.value.toDaysCoffeesList())
    }
}
