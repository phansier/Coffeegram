package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.TableScreenIntent
import ru.beryukhov.coffeegram.model.TableScreenState
import ru.beryukhov.coffeegram.model.TableScreenStore

interface TableComponent {

    val models: StateFlow<TableScreenState>

    fun onIncrementMonth()
    fun onDecrementMonth()
    fun onDayClick(dayOfMonth: Int)

    // fun onNavigate(child: KClass<out RootComponent.Child>)
}

class DefaultTableComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
    val tableScreenStore: TableScreenStore = TableScreenStore(
        initialStoreState = daysCoffeesStore.state.value
    ), // todo move into DI
) : TableComponent, ComponentContext by context {

    override val models: StateFlow<TableScreenState> = tableScreenStore.state

    init {
        daysCoffeesStore.state.onEach {
            tableScreenStore.newIntent(TableScreenIntent.NewDaysCoffeesState(it))
        }.launchIn(CoroutineScope(Dispatchers.Default + SupervisorJob()))
    }

    override fun onIncrementMonth() {
        tableScreenStore.newIntent(TableScreenIntent.NextMonth)
    }

    override fun onDecrementMonth() {
        tableScreenStore.newIntent(TableScreenIntent.PreviousMonth)
    }

    override fun onDayClick(dayOfMonth: Int) {
        println("onDayClick $dayOfMonth")
    }

//    override fun onNavigate(child: KClass<out RootComponent.Child>) {}
}
