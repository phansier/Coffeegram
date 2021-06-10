package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.date_time.local_date.LocalDate
import ru.beryukhov.coffeegram.date_time.local_date.monthValue
import ru.beryukhov.coffeegram.date_time.local_date.of
import ru.beryukhov.coffeegram.date_time.local_date.year
import ru.beryukhov.coffeegram.date_time.year_month.YearMonth
import ru.beryukhov.coffeegram.date_time.year_month.minusMonths
import ru.beryukhov.coffeegram.date_time.year_month.monthValue
import ru.beryukhov.coffeegram.date_time.year_month.now
import ru.beryukhov.coffeegram.date_time.year_month.of
import ru.beryukhov.coffeegram.date_time.year_month.plusMonths
import ru.beryukhov.coffeegram.date_time.year_month.year


class NavigationStore : Store<NavigationIntent, NavigationState>(
        initialState = NavigationState.TablePage(now())
    ) {


    override fun handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            NavigationIntent.NextMonth -> {
                increaseMonth(stateFlow.value.yearMonth)
            }
            NavigationIntent.PreviousMonth -> {
                decreaseMonth(stateFlow.value.yearMonth)
            }
            is NavigationIntent.OpenCoffeeListPage -> {
                NavigationState.CoffeeListPage(
                    of(
                        stateFlow.value.yearMonth.year,
                        stateFlow.value.yearMonth.monthValue,
                        intent.dayOfMonth
                    )
                )
            }
            NavigationIntent.ReturnToTablePage -> {
                NavigationState.TablePage(stateFlow.value.yearMonth)
            }
        }
    }

    private fun increaseMonth(yearMonth: YearMonth): NavigationState {
        return NavigationState.TablePage(yearMonth.plusMonths(1))
    }

    private fun decreaseMonth(yearMonth: YearMonth): NavigationState {
        return NavigationState.TablePage(yearMonth.minusMonths(1))
    }
}

sealed class NavigationIntent {
    object NextMonth : NavigationIntent()
    object PreviousMonth : NavigationIntent()
    data class OpenCoffeeListPage(val dayOfMonth: Int) : NavigationIntent()
    object ReturnToTablePage : NavigationIntent()
}

sealed class NavigationState(val yearMonth: YearMonth) {
    class TablePage(yearMonth: YearMonth) : NavigationState(yearMonth)
    data class CoffeeListPage(val date: LocalDate) : NavigationState(
        of(date.year, date.monthValue)
    )
}