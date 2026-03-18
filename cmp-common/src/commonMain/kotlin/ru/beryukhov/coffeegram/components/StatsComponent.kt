package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

/**
 * Component for statistics/charts feature.
 * Wraps DaysCoffeesStore and exposes state for chart rendering.
 */
interface StatsComponent {
    val models: StateFlow<DaysCoffeesState>
}

class DefaultStatsComponent(
    context: ComponentContext,
    private val daysCoffeesStore: DaysCoffeesStore,
) : StatsComponent, ComponentContext by context {

    override val models: StateFlow<DaysCoffeesState> = daysCoffeesStore.state
}
