package ru.beryukhov.coffeegram.data

import ru.beryukhov.coffeegram.data.icons.Coffee
import ru.beryukhov.coffeegram.data.icons.CoffeeIcons

data class DayCoffee(val coffeeCountMap: Map<CoffeeType, Int> = mapOf())

fun DayCoffee.getDayIconCoffeeType(): Picture {
    val t = coffeeCountMap.filterValues { it > 0 }
    return when {
        t.isEmpty() -> Picture.EMPTY
        t.size == 1 -> t.keys.first().icon
        else -> Vector(CoffeeIcons.Coffee)
    }
}
