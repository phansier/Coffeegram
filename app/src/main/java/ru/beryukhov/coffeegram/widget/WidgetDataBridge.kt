package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Bridge between Glance widget and Store pattern.
 * Provides widget-specific data access to the DaysCoffeesStore.
 */
interface WidgetDataBridge {
    fun getCurrentDayCupsCount(): Int
    fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount
    fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount>
    fun incrementCoffee(coffeeType: CoffeeType)
    fun decrementCoffee(coffeeType: CoffeeType)
}

class DefaultWidgetDataBridge(
    private val daysCoffeesStore: DaysCoffeesStore,
) : WidgetDataBridge {

    @OptIn(ExperimentalTime::class)
    private fun getCurrentDay() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun getCurrentDayCupsCount(): Int {
        return getCurrentDayList().sumOf { it.count }
    }

    override fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount {
        val list = getCurrentDayList()
        return if (list.isEmpty()) {
            CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 0)
        } else {
            list.first()
        }
    }

    override fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount> {
        val dayCoffeeState: DaysCoffeesState = daysCoffeesStore.state.value
        val dayCoffee = dayCoffeeState.coffees[getCurrentDay()] ?: DayCoffee()
        val list = dayCoffee.coffeeCountMap.withEmpty().toList()
            .sortedByDescending { it.count }
        return list.toPersistentList()
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
