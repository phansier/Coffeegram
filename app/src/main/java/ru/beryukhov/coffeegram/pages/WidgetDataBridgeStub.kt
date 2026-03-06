package ru.beryukhov.coffeegram.pages

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.widget.WidgetDataBridge

object WidgetDataBridgeStub : WidgetDataBridge {
    override fun getCurrentDayCupsCount(): Flow<Int> = flowOf(0)

    override fun getCurrentDayMostPopularWithCount(): Flow<CoffeeTypeWithCount> =
        flowOf(mockList.first())

    override fun getCurrentDayList(): Flow<PersistentList<CoffeeTypeWithCount>> =
        flowOf(mockList)

    override fun incrementCoffee(coffeeType: CoffeeType) = Unit

    override fun decrementCoffee(coffeeType: CoffeeType) = Unit
}

private val mockList: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeTypes.Espresso, 3),
    CoffeeTypeWithCount(CoffeeTypes.Macchiato, 2),
)
