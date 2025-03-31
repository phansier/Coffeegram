package ru.beryukhov.coffeegram.pages

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Clock
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

interface AppWidgetViewModel {
    fun getCurrentDayCupsCount(): Int
    fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount
    fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount>

    fun currentDayIncrement(coffeeType: CoffeeType)
    fun currentDayDecrement(coffeeType: CoffeeType)
}

class AppWidgetViewModelStub : AppWidgetViewModel {
    override fun getCurrentDayCupsCount(): Int = 0

    override fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount =
        mockList.first()

    override fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount> =
        mockList

    override fun currentDayIncrement(coffeeType: CoffeeType) = Unit

    override fun currentDayDecrement(coffeeType: CoffeeType) = Unit
}

private val mockList: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeTypes.Espresso, 3),
    CoffeeTypeWithCount(CoffeeTypes.Macchiato, 2),
)

class AppWidgetViewModelImpl(
    private val daysCoffeesStore: DaysCoffeesStore,
) : ViewModel(), AppWidgetViewModel {

    private fun getCurrentDay() = Clock.System.todayIn(TimeZone.currentSystemDefault())

    override fun getCurrentDayCupsCount(): Int {
        return getCurrentDayList().sumOf { it.count }
    }

    override fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount {
        return getCurrentDayList().first()
    }

    // coffee list does not contains 0 values and sorted by count
    override fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount> {
        val dayCoffeeState: DaysCoffeesState = daysCoffeesStore.state.value
        val dayCoffee = dayCoffeeState.coffees[getCurrentDay()] ?: DayCoffee()
        val list = dayCoffee.coffeeCountMap.withEmpty().toList()
            .sortedByDescending { it.count }
        val emptyListMock = listOf(CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 0))
        return list.toPersistentList()
    }

    override fun currentDayDecrement(coffeeType: CoffeeType) {
        newIntent(
            DaysCoffeesIntent.MinusCoffee(
                localDate = getCurrentDay(),
                coffeeType = coffeeType
            )
        )
    }

    override fun currentDayIncrement(coffeeType: CoffeeType) {
        newIntent(
            DaysCoffeesIntent.PlusCoffee(
                localDate = getCurrentDay(),
                coffeeType = coffeeType
            )
        )
    }

    private fun newIntent(intent: DaysCoffeesIntent) {
        daysCoffeesStore.newIntent(intent)
    }
}
