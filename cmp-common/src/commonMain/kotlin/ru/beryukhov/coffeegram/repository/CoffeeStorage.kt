@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.repository

import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import repository.CoffeeRepository
import repository.model.DbDayCoffee
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
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

fun List<DbDayCoffee>.toState(): DaysCoffeesState {
    val map = mutableMapOf<LocalDate, DayCoffee>()
    this.forEach {
        val date: LocalDate = LocalDate.parse(it.date)
        val dayCoffee1: Map<CoffeeType, Int> = map[date]?.let { mapDate ->
            val dayCoffee: MutableMap<CoffeeType, Int> = mapDate.coffeeCountMap.toMutableMap()
            dayCoffee[CoffeeTypes.valueOf(it.coffeeName)] = it.count
            dayCoffee
        } ?: mapOf(CoffeeTypes.valueOf(it.coffeeName) to it.count)
        map[date] = DayCoffee(dayCoffee1)
    }
    return DaysCoffeesState(map)
}

fun Map<LocalDate, DayCoffee>.toDaysCoffeesList(): List<DbDayCoffee> {
    val list = mutableListOf<DbDayCoffee>()
    this.forEach { entry: Map.Entry<LocalDate, DayCoffee> ->
        val date = entry.key.toString()
        val dayCoffee: DayCoffee = entry.value
        dayCoffee.coffeeCountMap.forEach { inner_entry: Map.Entry<CoffeeType, Int> ->
            list.add(
                DbDayCoffee(
                    date = date,
                    coffeeName = inner_entry.key.dbKey,
                    count = inner_entry.value
                )
            )
        }
    }
    return list
}
