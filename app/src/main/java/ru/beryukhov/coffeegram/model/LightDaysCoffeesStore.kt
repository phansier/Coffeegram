package ru.beryukhov.coffeegram.model

import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class LightDaysCoffeesStore : InMemoryStore<DaysCoffeesIntent, DaysCoffeesState>(
    initialState = DaysCoffeesState(mapOf())
), DaysCoffeesStore {

    override suspend fun handleIntent(intent: DaysCoffeesIntent): DaysCoffeesState {
        return when (intent) {
            is DaysCoffeesIntent.PlusCoffee -> increaseCoffee(intent.localDate, intent.coffeeType)
            is DaysCoffeesIntent.MinusCoffee -> decreaseCoffee(intent.localDate, intent.coffeeType)
        }
    }

    private fun increaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.plus(1) ?: 1
        )
    }

    private fun decreaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState {
        return putCoffeeCount(
            localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.minus(1) ?: 0
        )
    }

    private fun getCoffeeOrNull(localDate: LocalDate, coffeeType: CoffeeType): Int? {
        return state.value.value[localDate]?.coffeeCountMap?.get(coffeeType)
    }

    private fun putCoffeeCount(localDate: LocalDate, coffeeType: CoffeeType, count: Int): DaysCoffeesState {
        return state.value.copy(
            value = state.value.value.toMutableMap().also {
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
