package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.MonthTableScreenIntent
import ru.beryukhov.coffeegram.model.MonthTableScreenState
import ru.beryukhov.coffeegram.model.MonthTableScreenStore

interface MonthTableComponent {
    val models: StateFlow<MonthTableScreenState>

    fun onIncrementMonth()
    fun onDecrementMonth()
    fun onDayClick(dayOfMonth: Int)
}

class DefaultMonthTableComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
    val monthTableScreenStore: MonthTableScreenStore = MonthTableScreenStore(
        initialStoreState = daysCoffeesStore.state.value
    ), // todo move into DI
    val onNavigate: (LocalDate) -> Unit,
) : MonthTableComponent, ComponentContext by context {

    override val models: StateFlow<MonthTableScreenState> = monthTableScreenStore.state

    init {
        daysCoffeesStore.state.onEach {
            monthTableScreenStore.newIntent(MonthTableScreenIntent.NewDaysCoffeesState(it))
        }.launchIn(CoroutineScope(Dispatchers.Default + SupervisorJob()))
    }

    override fun onIncrementMonth() {
        monthTableScreenStore.newIntent(MonthTableScreenIntent.NextMonth)
    }

    override fun onDecrementMonth() {
        monthTableScreenStore.newIntent(MonthTableScreenIntent.PreviousMonth)
    }

    override fun onDayClick(dayOfMonth: Int) {
        onNavigate(models.value.yearMonth.atDay(dayOfMonth))
    }
}
