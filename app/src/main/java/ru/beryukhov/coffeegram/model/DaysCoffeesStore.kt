package ru.beryukhov.coffeegram.model

import androidx.compose.runtime.State
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee

class DaysCoffeesStore : Store<DaysCoffeesIntent, DaysCoffeesState>(
    initialState = DaysCoffeesState(mapOf())
) {

    override fun handleIntent(intent: DaysCoffeesIntent): DaysCoffeesState {
        return when (intent) {
            is DaysCoffeesIntent.PlusCoffee -> increaseCoffee(intent.localDate, intent.coffeeType)
            is DaysCoffeesIntent.MinusCoffee -> decreaseCoffee(intent.localDate, intent.coffeeType)
        }
    }

    private fun increaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState{
        return putCoffeeCount(localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.plus(1)?:1
        )
    }

    private fun decreaseCoffee(localDate: LocalDate, coffeeType: CoffeeType): DaysCoffeesState{
        return putCoffeeCount(localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.minus(1)?:0
        )
    }

    private fun getCoffeeOrNull(localDate: LocalDate, coffeeType: CoffeeType): Int? {
        return _state.value.value[localDate]?.coffeeCountMap?.get(coffeeType)
    }

    private fun putCoffeeCount(localDate: LocalDate, coffeeType: CoffeeType, count: Int): DaysCoffeesState {
        return _state.value.copy(
            value = _state.value.value.toMutableMap().also{
                if (it[localDate]==null){
                    it[localDate] = DayCoffee()
                }
                val countMap: MutableMap<CoffeeType, Int> = it[localDate]!!.coffeeCountMap.toMutableMap()
                countMap[coffeeType] = count
                it[localDate] = DayCoffee(countMap)
            }
        )
    }

}

sealed class DaysCoffeesIntent {
    data class PlusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) : DaysCoffeesIntent()
    data class MinusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) : DaysCoffeesIntent()
}

data class DaysCoffeesState(override val value: Map<LocalDate, DayCoffee>): State<Map<LocalDate, DayCoffee>>