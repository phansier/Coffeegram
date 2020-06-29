package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class NavigationStore: Store<NavigationIntent, NavigationState> {
    private val _intentChannel = Channel<NavigationIntent>(Channel.UNLIMITED)
    private val _state =
        MutableStateFlow<NavigationState>(
            NavigationState.TablePage(YearMonth.now())
        )
    val state: StateFlow<NavigationState>
        get() = _state

    fun newIntent(navigationIntent: NavigationIntent){
        _intentChannel.offer(navigationIntent)
    }

    init {
        GlobalScope.launch {
            handleIntents()
        }
    }

    private suspend fun handleIntents() {
        _intentChannel.consumeAsFlow().collect { intent ->

            when (intent) {
                NavigationIntent.NextMonth -> {
                    _state.value = increaseMonth(_state.value.yearMonth)
                }
                NavigationIntent.PreviousMonth -> {
                    _state.value = decreaseMonth(_state.value.yearMonth)
                }
                is NavigationIntent.OpenCoffeeListPage -> {
                    _state.value = NavigationState.CoffeeListPage(
                        LocalDate.of(
                            _state.value.yearMonth.year,
                            _state.value.yearMonth.month,
                            intent.dayOfMonth
                        )
                    )
                }
                NavigationIntent.ReturnToTablePage -> {
                    _state.value = NavigationState.TablePage(_state.value.yearMonth)
                }
            }
        }
    }

    private fun increaseMonth(yearMonth: YearMonth): NavigationState{
        return NavigationState.TablePage(yearMonth.plusMonths(1))
    }

    private fun decreaseMonth(yearMonth: YearMonth): NavigationState{
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