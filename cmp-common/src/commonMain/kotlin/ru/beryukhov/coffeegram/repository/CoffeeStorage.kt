package ru.beryukhov.coffeegram.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDate.Companion.parse
import repository.CoffeeRepository
import repository.model.DbDayCoffee
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.coffeeTypeValueOf
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.store_lib.Storage

class CoffeeStorage(private val repository: CoffeeRepository) :
    Storage<DaysCoffeesState> {
    override suspend fun getState(): DaysCoffeesState {
        return repository.getAll().toState()
    }

    override suspend fun saveState(state: DaysCoffeesState) {
        repository.createOrUpdate(state.coffees.toDaysCoffeesList())
    }
}

// @VisibleForTesting
internal fun List<DbDayCoffee>.toState(): DaysCoffeesState {
    val map = mutableMapOf<LocalDate, DayCoffee>()
    this.forEach {
        val date: LocalDate = parse(it.date)
        val dayCoffee1 = map[date]?.let { map_date ->
            val dayCoffee: MutableMap<CoffeeType, Int> = map_date.coffeeCountMap.toMutableMap()
            dayCoffee[coffeeTypeValueOf(it.coffeeName)] = it.count
            dayCoffee
        } ?: mapOf(coffeeTypeValueOf(it.coffeeName) to it.count)
        map[date] = DayCoffee(dayCoffee1)
    }
    return DaysCoffeesState(map)
}

// @VisibleForTesting
internal fun Map<LocalDate, DayCoffee>.toDaysCoffeesList(): List<DbDayCoffee> {
    val list = mutableListOf<DbDayCoffee>()
    this.forEach { entry: Map.Entry<LocalDate, DayCoffee> ->
        val date = entry.key.toString()
        val dayCoffee: DayCoffee = entry.value
        dayCoffee.coffeeCountMap.forEach { inner_entry: Map.Entry<CoffeeType, Int> ->
            list.add(
                DbDayCoffee(
                    date = date,
                    coffeeName = inner_entry.key.name,
                    count = inner_entry.value
                )
            )
        }
    }
    return list
}
