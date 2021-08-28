package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class NavigationStore(val yearMonth: YearMonth = YearMonth.now()) : InMemoryStore<NavigationIntent, NavigationState>(
    initialState = NavigationState.TablePage(yearMonth = yearMonth)
) {

    private val currentMonth = MutableStateFlow(YearMonth.now())

    override suspend fun handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            NavigationIntent.NextMonth -> {
                NavigationState.TablePage(increaseMonth())

            }
            NavigationIntent.PreviousMonth -> {
                NavigationState.TablePage(decreaseMonth())
            }
            is NavigationIntent.OpenCoffeeListPage -> {
                NavigationState.CoffeeListPage(
                    LocalDate.of(
                        currentMonth.value.year,
                        currentMonth.value.month,
                        intent.dayOfMonth
                    )
                )
            }
            NavigationIntent.ReturnToTablePage -> {
                NavigationState.TablePage(currentMonth.value)
            }
            NavigationIntent.ToSettingsPage -> {
                NavigationState.SettingsPage
            }
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


sealed class NavigationIntent {
    object NextMonth : NavigationIntent()
    object PreviousMonth : NavigationIntent()
    data class OpenCoffeeListPage(val dayOfMonth: Int) : NavigationIntent()
    object ReturnToTablePage : NavigationIntent()
    object ToSettingsPage : NavigationIntent()
}

sealed class NavigationState {
    class TablePage(val yearMonth: YearMonth) : NavigationState()
    data class CoffeeListPage(val date: LocalDate) : NavigationState()
    object SettingsPage : NavigationState()
}