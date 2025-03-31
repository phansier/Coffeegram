package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.store_lib.InMemoryStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

class NavigationStore(val yearMonth: YearMonth = nowYM()) : InMemoryStore<NavigationIntent, NavigationState>(
    initialState = NavigationState.TablePage(yearMonth = yearMonth)
) {

    private val currentYearMonth = MutableStateFlow(nowYM())

    override fun NavigationState.handleIntent(intent: NavigationIntent): NavigationState {
        return when (intent) {
            is NavigationIntent.OpenCoffeeListPage -> NavigationState.CoffeeListPage(
                LocalDate(
                    year = currentYearMonth.value.year,
                    month = currentYearMonth.value.month,
                    dayOfMonth = intent.dayOfMonth
                )
            )
            is NavigationIntent.SetYearMonth -> NavigationState.TablePage(
                setYearMonth(yearMonth = intent.yearMonth)
            )
            NavigationIntent.ReturnToTablePage -> NavigationState.TablePage(currentYearMonth.value)
            NavigationIntent.ToStatsPage -> NavigationState.StatsPage
            NavigationIntent.ToSettingsPage -> NavigationState.SettingsPage
            NavigationIntent.ToMapPage -> NavigationState.MapPage
        }
    }

    private fun setYearMonth(yearMonth: YearMonth): YearMonth {
        currentYearMonth.value = yearMonth
        return currentYearMonth.value
    }
}

sealed interface NavigationIntent {
    data class OpenCoffeeListPage(val dayOfMonth: Int) : NavigationIntent
    data class SetYearMonth(val yearMonth: YearMonth) : NavigationIntent
    data object ReturnToTablePage : NavigationIntent
    data object ToStatsPage : NavigationIntent
    data object ToSettingsPage : NavigationIntent
    data object ToMapPage : NavigationIntent
}

sealed interface NavigationState {
    fun mapTestTag(): String = when (this) {
        is CoffeeListPage -> "CoffeeListScreen"
        MapPage -> "MapScreen"
        StatsPage -> "StatsScreen"
        SettingsPage -> "SettingsScreen"
        is TablePage -> "TableScreen"
    }

    class TablePage(val yearMonth: YearMonth) : NavigationState
    data class CoffeeListPage(val date: LocalDate) : NavigationState
    data object StatsPage : NavigationState
    data object SettingsPage : NavigationState
    data object MapPage : NavigationState
    companion object {
        const val NAVIGATION_STATE_KEY = "NavigationState"
        const val TODAYS_COFFEE_LIST = "TodaysCoffeeList"
    }
}
