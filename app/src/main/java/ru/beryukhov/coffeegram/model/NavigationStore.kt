package ru.beryukhov.coffeegram.model

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class NavigationStore : Store<NavigationIntent, NavigationState>(
        initialState = NavigationState.TablePage(YearMonth.now())
    ) {


    override fun handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            NavigationIntent.NextMonth -> {
                increaseMonth(_state.value.yearMonth)
            }
            NavigationIntent.PreviousMonth -> {
                decreaseMonth(_state.value.yearMonth)
            }
            is NavigationIntent.OpenCoffeeListPage -> {
                NavigationState.CoffeeListPage(
                    LocalDate.of(
                        _state.value.yearMonth.year,
                        _state.value.yearMonth.month,
                        intent.dayOfMonth
                    )
                )
            }
            NavigationIntent.ReturnToTablePage -> {
                NavigationState.TablePage(_state.value.yearMonth)
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
        YearMonth.of(date.year, date.month)
    )
}