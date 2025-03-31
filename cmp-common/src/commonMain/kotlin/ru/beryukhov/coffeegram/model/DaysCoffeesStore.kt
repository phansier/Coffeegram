package ru.beryukhov.coffeegram.model

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.store_lib.Store
import ru.beryukhov.coffeegram.store_lib.StoreImpl

interface DaysCoffeesStore : Store<DaysCoffeesIntent, DaysCoffeesState>

// todo make internal after apps merge will be finished
class DaysCoffeesStoreImpl(coffeeStorage: CoffeeStorage) : DaysCoffeesStore,
    StoreImpl<DaysCoffeesIntent, DaysCoffeesState>(
        initialState = DaysCoffeesState(),
        storage = coffeeStorage
    ) {

    override fun DaysCoffeesState.handleIntent(intent: DaysCoffeesIntent): DaysCoffeesState {
        return when (intent) {
            is DaysCoffeesIntent.PlusCoffee -> increaseCoffee(intent.localDate, intent.coffeeType)
            is DaysCoffeesIntent.MinusCoffee -> decreaseCoffee(intent.localDate, intent.coffeeType)
        }
    }

    private fun DaysCoffeesState.increaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate = localDate,
            coffeeType = coffeeType,
            count = getCoffeeOrNull(localDate, coffeeType)?.plus(1) ?: 1
        )
    }

    private fun DaysCoffeesState.decreaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate = localDate,
            coffeeType = coffeeType,
            count = getCoffeeOrNull(localDate, coffeeType)?.minus(1) ?: 0
        )
    }

    private fun DaysCoffeesState.getCoffeeOrNull(localDate: LocalDate, coffeeType: CoffeeType): Int? {
        return coffees[localDate]?.coffeeCountMap?.get(coffeeType)
    }

    private fun DaysCoffeesState.putCoffeeCount(
        localDate: LocalDate,
        coffeeType: CoffeeType,
        count: Int
    ): DaysCoffeesState {
        return DaysCoffeesState(
            changeCoffeeCount(
                oldValue = coffees,
                localDate = localDate,
                coffeeType = coffeeType,
                count = count
            )
        )
    }
}

// @VisibleForTesting
fun changeCoffeeCount(
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
