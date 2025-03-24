package ru.beryukhov.coffeegram.model

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.getDayIconCoffeeType
import ru.beryukhov.coffeegram.store_lib.InMemoryStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

class TableScreenStore(yearMonth: YearMonth = nowYM(), initialStoreState: DaysCoffeesState) :
    InMemoryStore<TableScreenIntent, TableScreenState>(
        initialState = TableScreenState(
            yearMonth = yearMonth,
            daysCoffeesState = initialStoreState,
            filledDayItemsMap = initialStoreState.calculate(yearMonth)
        )
    ) {

    override fun TableScreenState.handleIntent(intent: TableScreenIntent): TableScreenState =
        when (intent) {
            TableScreenIntent.NextMonth ->
                copy(
                    yearMonth = increaseMonth(),
                    filledDayItemsMap = daysCoffeesState.calculate(increaseMonth())
                )

            TableScreenIntent.PreviousMonth ->
                copy(
                    yearMonth = decreaseMonth(),
                    filledDayItemsMap = daysCoffeesState.calculate(decreaseMonth())
                )

            is TableScreenIntent.NewDaysCoffeesState ->
                copy(
                    daysCoffeesState = intent.state,
                    filledDayItemsMap = intent.state.calculate(yearMonth)
                )
        }

    private fun TableScreenState.increaseMonth(): YearMonth {
        return this.yearMonth.plusMonths(1)
    }

    private fun TableScreenState.decreaseMonth(): YearMonth {
        return this.yearMonth.minusMonths(1)
    }
}

internal fun DaysCoffeesState.calculate(yearMonth: YearMonth): PersistentMap<Int, CoffeeType?> =
    this.coffees.filter { entry: Map.Entry<LocalDate, DayCoffee> ->
        entry.key.year == yearMonth.year && entry.key.month == yearMonth.month
    }
        .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
        .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getDayIconCoffeeType() }
        .toPersistentMap()

sealed interface TableScreenIntent {
    object NextMonth : TableScreenIntent
    object PreviousMonth : TableScreenIntent
    data class NewDaysCoffeesState(val state: DaysCoffeesState) : TableScreenIntent
}

data class TableScreenState(
    val yearMonth: YearMonth,
    val daysCoffeesState: DaysCoffeesState,
    val filledDayItemsMap: PersistentMap<Int, CoffeeType?>,
)
