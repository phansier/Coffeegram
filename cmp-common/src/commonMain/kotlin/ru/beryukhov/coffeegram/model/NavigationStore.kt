package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class NavigationStore(yearMonth: YearMonth = nowYM()) : InMemoryStore<NavigationIntent, NavigationState>(
    initialState = NavigationState.TablePage(yearMonth = yearMonth)
) {
    private val currentMonth = MutableStateFlow(nowYM())

    override fun handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            NavigationIntent.NextMonth -> NavigationState.TablePage(increaseMonth())
            NavigationIntent.PreviousMonth -> NavigationState.TablePage(decreaseMonth())

            is NavigationIntent.OpenCoffeeListPage -> NavigationState.CoffeeListPage(
                LocalDate(
                    year = currentMonth.value.year,
                    month = currentMonth.value.month,
                    dayOfMonth = intent.dayOfMonth
                )
            )
            NavigationIntent.ReturnToTablePage -> NavigationState.TablePage(currentMonth.value)
            NavigationIntent.ToSettingsPage -> NavigationState.SettingsPage
        }
    }

    private fun increaseMonth(): YearMonth {
        currentMonth.value = currentMonth.value.plusMonths(1)
        return currentMonth.value
    }

    private fun decreaseMonth(): YearMonth {
        currentMonth.value = currentMonth.value.minusMonths(1)
        return currentMonth.value
    }
}

sealed interface NavigationIntent {
    object NextMonth : NavigationIntent
    object PreviousMonth : NavigationIntent
    data class OpenCoffeeListPage(val dayOfMonth: Int) : NavigationIntent
    object ReturnToTablePage : NavigationIntent
    object ToSettingsPage : NavigationIntent
}

sealed interface NavigationState {
    class TablePage(val yearMonth: YearMonth) : NavigationState
    data class CoffeeListPage(val date: LocalDate) : NavigationState
    object SettingsPage : NavigationState
}
