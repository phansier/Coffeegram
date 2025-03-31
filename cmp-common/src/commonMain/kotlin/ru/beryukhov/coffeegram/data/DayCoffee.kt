package ru.beryukhov.coffeegram.data

import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.coffee

data class DayCoffee(val coffeeCountMap: Map<CoffeeType, Int> = mapOf())

fun DayCoffee.getDayIconCoffeeType(): Picture {
    val t = coffeeCountMap.filterValues { it > 0 }
    return when {
        t.isEmpty() -> Picture.EMPTY
        t.size == 1 -> t.keys.first().icon
        else -> Image(Res.drawable.coffee)
    }
}
