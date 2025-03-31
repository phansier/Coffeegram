package ru.beryukhov.coffeegram.model

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class LightDaysCoffeesStore : InMemoryStore<DaysCoffeesIntent, DaysCoffeesState>(
    initialState = DaysCoffeesState(mapOf())
), DaysCoffeesStore {

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
        return copy(
            coffees = coffees.toMutableMap().also {
                if (it[localDate] == null) {
                    it[localDate] = DayCoffee()
                }
                val countMap: MutableMap<CoffeeType, Int> = it[localDate]!!.coffeeCountMap.toMutableMap()
                countMap[coffeeType] = count
                it[localDate] = DayCoffee(countMap)
            }
        )
    }
}
