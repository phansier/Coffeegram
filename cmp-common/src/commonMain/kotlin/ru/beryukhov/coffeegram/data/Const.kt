package ru.beryukhov.coffeegram.data

const val START_ACTIVITY_PATH = "/start-activity"
const val DAY_COFFEE_PATH = "/coffee"

/**
 * Map contains only non-zero values
 * This function creates a list with all CoffeeTypes and their counts according to data from the map
 */
fun Map<CoffeeType, Int>.withEmpty(): List<CoffeeTypeWithCount> {
    @Suppress("DataClassShouldBeImmutable")
    data class MutablePair(val ct: CoffeeType, var count: Int)

    val emptyList: MutableList<MutablePair> =
        CoffeeTypes.entries.map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { CoffeeTypeWithCount(it.ct, it.count) }
}
