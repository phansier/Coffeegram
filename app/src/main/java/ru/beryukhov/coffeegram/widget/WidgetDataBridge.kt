package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import kotlin.time.Clock

/**
 * A coffee row with everything the widget needs already resolved, so that rendering never
 * has to suspend or wait for a recomposition. Glance previews compose exactly once.
 */
data class WidgetCoffee(
    val type: CoffeeType,
    val name: String,
    val count: Int,
)

suspend fun List<CoffeeTypeWithCount>.toWidgetCoffees(): PersistentList<WidgetCoffee> =
    map { WidgetCoffee(it.coffee, it.coffee.localizedName.get(), it.count) }.toPersistentList()

/**
 * Bridge between Glance widget and Store pattern.
 * Provides widget-specific data access to the DaysCoffeesStore.
 */
interface WidgetDataBridge {
    fun getCurrentDayList(): Flow<PersistentList<WidgetCoffee>>
    fun incrementCoffee(coffeeType: CoffeeType)
    fun decrementCoffee(coffeeType: CoffeeType)
}

class DefaultWidgetDataBridge(
    private val daysCoffeesStore: DaysCoffeesStore,
) : WidgetDataBridge {

    private fun getCurrentDay() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun getCurrentDayList(): Flow<PersistentList<WidgetCoffee>> =
        daysCoffeesStore.state.map { dayCoffeeState ->
            val dayCoffee = dayCoffeeState.coffees[getCurrentDay()] ?: DayCoffee()
            dayCoffee.coffeeCountMap.withEmpty()
                .sortedByDescending { it.count }
                .toWidgetCoffees()
        }

    override fun incrementCoffee(coffeeType: CoffeeType) {
        daysCoffeesStore.newIntent(
            DaysCoffeesIntent.PlusCoffee(
                localDate = getCurrentDay(),
                coffeeType = coffeeType
            )
        )
    }

    override fun decrementCoffee(coffeeType: CoffeeType) {
        daysCoffeesStore.newIntent(
            DaysCoffeesIntent.MinusCoffee(
                localDate = getCurrentDay(),
                coffeeType = coffeeType
            )
        )
    }
}
