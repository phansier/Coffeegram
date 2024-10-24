package ru.beryukhov.coffeegram.pages

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DayCoffee
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
        CoffeeTypeWithCount(CoffeeType.Cappuccino, 0)

    override fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount> =
        persistentListOf(CoffeeTypeWithCount(CoffeeType.Cappuccino, 0))

    override fun currentDayIncrement(coffeeType: CoffeeType) = Unit

    override fun currentDayDecrement(coffeeType: CoffeeType) = Unit
}

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
        val dayCoffee = dayCoffeeState.value[getCurrentDay()] ?: DayCoffee()
        val list = dayCoffee.coffeeCountMap.toList()
            .sortedByDescending { it.second }
            .map { CoffeeTypeWithCount(it.first, it.second) }
        val emptyListMock = listOf(CoffeeTypeWithCount(CoffeeType.Cappuccino, 0))
        return list.ifEmpty { emptyListMock }.toPersistentList()
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
        Log.d("TEST||", "currentDayIncrement: coffeeType = $coffeeType")
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
