package ru.beryukhov.coffeegram.data

import ru.beryukhov.coffeegram.common.R

data class DayCoffee(
    val coffeeCountMap: Map<CoffeeType, Int> = mapOf()
) {
    fun getIconId(): Int? {
        val t = coffeeCountMap.filterValues { it > 0 }
        return when {
            t.isEmpty() -> null
            t.size == 1 -> t.keys.first().iconId
            else -> R.drawable.coffee
        }
    }
}
