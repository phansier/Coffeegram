package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.store_lib.InMemoryStore

class NavigationStore(val yearMonth: YearMonth = YearMonth.now()) : InMemoryStore<NavigationIntent, NavigationState>(
    initialState = NavigationState.TablePage(yearMonth = yearMonth)
) {

    private val currentYearMonth = MutableStateFlow(YearMonth.now())

    override suspend fun handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            is NavigationIntent.OpenCoffeeListPage -> NavigationState.CoffeeListPage(
                LocalDate.of(
                    currentYearMonth.value.year,
                    currentYearMonth.value.month,
                    intent.dayOfMonth
                )
            )
            is NavigationIntent.SetYearMonth -> NavigationState.TablePage(
                setYearMonth(yearMonth = intent.yearMonth)
            )
            NavigationIntent.ReturnToTablePage -> NavigationState.TablePage(currentYearMonth.value)
            NavigationIntent.ToSettingsPage -> NavigationState.SettingsPage
        }
    }

    private fun setYearMonth(yearMonth: YearMonth) : YearMonth{
        currentYearMonth.value = yearMonth
        return currentYearMonth.value
    }
}

sealed interface NavigationIntent {
    data class OpenCoffeeListPage(val dayOfMonth: Int) : NavigationIntent
    data class SetYearMonth(val yearMonth: YearMonth): NavigationIntent
    object ReturnToTablePage : NavigationIntent
    object ToSettingsPage : NavigationIntent
}

sealed interface NavigationState {
    class TablePage(val yearMonth: YearMonth) : NavigationState
    data class CoffeeListPage(val date: LocalDate) : NavigationState
    object SettingsPage : NavigationState
}
