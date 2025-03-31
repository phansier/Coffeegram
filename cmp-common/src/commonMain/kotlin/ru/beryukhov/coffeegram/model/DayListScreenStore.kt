package ru.beryukhov.coffeegram.model

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class DayListScreenStore(date: LocalDate, initialStoreState: DaysCoffeesState) :
    InMemoryStore<DayListScreenIntent, DayListScreenState>(
        initialState = DayListScreenState(
            date = date,
            daysCoffeesState = initialStoreState,
            dayItems = initialStoreState.calculate(date)
        )
    ) {
    override fun DayListScreenState.handleIntent(intent: DayListScreenIntent): DayListScreenState =
        when (intent) {
            is DayListScreenIntent.NewDaysCoffeesState ->
                copy(
                    daysCoffeesState = intent.state,
                    dayItems = intent.state.calculate(date)
                )
        }
}

private fun DaysCoffeesState.calculate(date: LocalDate): List<CoffeeTypeWithCount> =
    (this.coffees[date] ?: DayCoffee()).coffeeCountMap.withEmpty()

sealed interface DayListScreenIntent {
    data class NewDaysCoffeesState(val state: DaysCoffeesState) : DayListScreenIntent
}

data class DayListScreenState(
    val date: LocalDate,
    val daysCoffeesState: DaysCoffeesState,
    val dayItems: List<CoffeeTypeWithCount>
)
