package ru.beryukhov.coffeegram.model

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.store_lib.PersistentStore
import ru.beryukhov.coffeegram.store_lib.Store

interface DaysCoffeesStore : Store<DaysCoffeesIntent, DaysCoffeesState>

internal class DaysCoffeesStoreImpl(coffeeStorage: CoffeeStorage) : DaysCoffeesStore,
    PersistentStore<DaysCoffeesIntent, DaysCoffeesState>(
        initialState = DaysCoffeesState(),
        storage = coffeeStorage
    ) {

    override fun handleIntent(intent: DaysCoffeesIntent): DaysCoffeesState {
        return when (intent) {
            is DaysCoffeesIntent.PlusCoffee -> increaseCoffee(intent.localDate, intent.coffeeType)
            is DaysCoffeesIntent.MinusCoffee -> decreaseCoffee(intent.localDate, intent.coffeeType)
        }
    }

    private fun increaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate = localDate,
            coffeeType = coffeeType,
            count = getCoffeeOrNull(localDate, coffeeType)?.plus(1) ?: 1
        )
    }

    private fun decreaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate = localDate,
            coffeeType = coffeeType,
            count = getCoffeeOrNull(localDate, coffeeType)?.minus(1) ?: 0
        )
    }

    private fun getCoffeeOrNull(localDate: LocalDate, coffeeType: CoffeeType): Int? {
        return state.value.coffees[localDate]?.coffeeCountMap?.get(coffeeType)
    }

    private fun putCoffeeCount(localDate: LocalDate, coffeeType: CoffeeType, count: Int): DaysCoffeesState {
        return DaysCoffeesState(
            changeCoffeeCount(
                oldValue = state.value.coffees,
                localDate = localDate,
                coffeeType = coffeeType,
                count = count
            )
        )
    }
}

// @VisibleForTesting
internal fun changeCoffeeCount(
    oldValue: Map<LocalDate, DayCoffee>,
    localDate: LocalDate,
    coffeeType: CoffeeType,
    count: Int
): Map<LocalDate, DayCoffee> {
    val newValue: MutableMap<LocalDate, DayCoffee> = oldValue.toMutableMap()
    newValue.also {
        val countMap: MutableMap<CoffeeType, Int> = if (it[localDate] == null) {
            mutableMapOf()
        } else {
            it[localDate]!!.coffeeCountMap.toMutableMap()
        }
        countMap[coffeeType] = count
        it[localDate] = DayCoffee(countMap)
    }
    return newValue
}

sealed class DaysCoffeesIntent {
    data class PlusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) : DaysCoffeesIntent()
    data class MinusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) : DaysCoffeesIntent()
}

data class DaysCoffeesState(val coffees: Map<LocalDate, DayCoffee> = mapOf())
