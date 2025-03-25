package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.DayListScreenIntent
import ru.beryukhov.coffeegram.model.DayListScreenState
import ru.beryukhov.coffeegram.model.DayListScreenStore
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

interface DayListComponent : BackHandlerOwner {
    val models: StateFlow<DayListScreenState>

    fun onMinusCoffee(coffeeType: CoffeeType)
    fun onPlusCoffee(coffeeType: CoffeeType)

    fun onBackClicked()
}

class DefaultDayListComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
    val date: LocalDate,
    val dayListScreenStore: DayListScreenStore = DayListScreenStore(
        date = date,
        initialStoreState = daysCoffeesStore.state.value
    ), // todo move into DI
    val onBackNavigation: () -> Unit,
) : DayListComponent, ComponentContext by context {
    override val models: StateFlow<DayListScreenState> = dayListScreenStore.state

    init {
        daysCoffeesStore.state.onEach {
            dayListScreenStore.newIntent(DayListScreenIntent.NewDaysCoffeesState(it))
        }.launchIn(CoroutineScope(Dispatchers.Default + SupervisorJob()))
    }

    override fun onMinusCoffee(coffeeType: CoffeeType) {
        daysCoffeesStore.newIntent(DaysCoffeesIntent.MinusCoffee(date, coffeeType))
    }

    override fun onPlusCoffee(coffeeType: CoffeeType) {
        daysCoffeesStore.newIntent(DaysCoffeesIntent.PlusCoffee(date, coffeeType))
    }

    override fun onBackClicked() {
        println("DayListComponentonBackClicked")
        onBackNavigation()
    }
}
