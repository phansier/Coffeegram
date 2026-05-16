package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import kotlin.time.Clock

/**
 * Bridge between Glance widget and Store pattern.
 * Provides widget-specific data access to the DaysCoffeesStore.
 */
interface WidgetDataBridge {
    fun getCurrentDayCupsCount(): Flow<Int>
    fun getCurrentDayMostPopularWithCount(): Flow<CoffeeTypeWithCount>
    fun getCurrentDayList(): Flow<PersistentList<CoffeeTypeWithCount>>
    fun incrementCoffee(coffeeType: CoffeeType)
    fun decrementCoffee(coffeeType: CoffeeType)
}

class DefaultWidgetDataBridge(
    private val daysCoffeesStore: DaysCoffeesStore,
) : WidgetDataBridge {

    private fun getCurrentDay() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun getCurrentDayCupsCount(): Flow<Int> =
        getCurrentDayList().map { it.sumOf { it.count } }

    override fun getCurrentDayMostPopularWithCount(): Flow<CoffeeTypeWithCount> = getCurrentDayList().map { list ->
        if (list.isEmpty()) {
            CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 0)
        } else {
            list.first()
        }
    }

    override fun getCurrentDayList(): Flow<PersistentList<CoffeeTypeWithCount>> =
        daysCoffeesStore.state.map { dayCoffeeState ->
            val dayCoffee = dayCoffeeState.coffees[getCurrentDay()] ?: DayCoffee()
            val list = dayCoffee.coffeeCountMap.withEmpty().toList()
                .sortedByDescending { it.count }
            list.toPersistentList()
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
