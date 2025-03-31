package ru.beryukhov.coffeegram.model

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.coffeegram.data.getDayIconCoffeeType
import ru.beryukhov.coffeegram.store_lib.InMemoryStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

class MonthTableScreenStore(yearMonth: YearMonth = nowYM(), initialStoreState: DaysCoffeesState) :
    InMemoryStore<MonthTableScreenIntent, MonthTableScreenState>(
        initialState = MonthTableScreenState(
            yearMonth = yearMonth,
            daysCoffeesState = initialStoreState,
            filledDayItemsMap = initialStoreState.calculate(yearMonth)
        )
    ) {

    override fun MonthTableScreenState.handleIntent(intent: MonthTableScreenIntent): MonthTableScreenState =
        when (intent) {
            MonthTableScreenIntent.NextMonth ->
                copy(
                    yearMonth = increaseMonth(),
                    filledDayItemsMap = daysCoffeesState.calculate(increaseMonth())
                )

            MonthTableScreenIntent.PreviousMonth ->
                copy(
                    yearMonth = decreaseMonth(),
                    filledDayItemsMap = daysCoffeesState.calculate(decreaseMonth())
                )

            is MonthTableScreenIntent.NewDaysCoffeesState ->
                copy(
                    daysCoffeesState = intent.state,
                    filledDayItemsMap = intent.state.calculate(yearMonth)
                )
        }

    private fun MonthTableScreenState.increaseMonth(): YearMonth {
        return this.yearMonth.plusMonths(1)
    }

    private fun MonthTableScreenState.decreaseMonth(): YearMonth {
        return this.yearMonth.minusMonths(1)
    }
}

internal fun DaysCoffeesState.calculate(yearMonth: YearMonth): PersistentMap<Int, Picture> =
    this.coffees.filter { entry: Map.Entry<LocalDate, DayCoffee> ->
        entry.key.year == yearMonth.year && entry.key.month == yearMonth.month
    }
        .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
        .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getDayIconCoffeeType() }
        .toPersistentMap()

sealed interface MonthTableScreenIntent {
    object NextMonth : MonthTableScreenIntent
    object PreviousMonth : MonthTableScreenIntent
    data class NewDaysCoffeesState(val state: DaysCoffeesState) : MonthTableScreenIntent
}

data class MonthTableScreenState(
    val yearMonth: YearMonth,
    val daysCoffeesState: DaysCoffeesState,
    val filledDayItemsMap: PersistentMap<Int, Picture>,
)
