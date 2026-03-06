package ru.beryukhov.coffeegram.pages

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.widget.WidgetDataBridge

object WidgetDataBridgeStub : WidgetDataBridge {
    override fun getCurrentDayCupsCount(): Int = 0

    override fun getCurrentDayMostPopularWithCount(): CoffeeTypeWithCount =
        mockList.first()

    override fun getCurrentDayList(): PersistentList<CoffeeTypeWithCount> =
        mockList

    override fun incrementCoffee(coffeeType: CoffeeType) = Unit

    override fun decrementCoffee(coffeeType: CoffeeType) = Unit
}

private val mockList: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeTypes.Espresso, 3),
    CoffeeTypeWithCount(CoffeeTypes.Macchiato, 2),
)
