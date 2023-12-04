package ru.beryukhov.coffeegram.data

data class DayCoffee(val coffeeCountMap: Map<CoffeeType, Int> = mapOf()) {
    fun getCoffeeType(): CoffeeType? {
        return getIconType(coffeeCountMap)
    }
}

fun getIconType(coffeeCountMap: Map<CoffeeType, Int>): CoffeeType? {
    val t = coffeeCountMap.filterValues { it > 0 }
    return when {
        t.isEmpty() -> null
        t.size == 1 -> t.keys.first()
        else -> CommonCoffee
    }
}
