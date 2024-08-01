package ru.beryukhov.coffeegram.data

import androidx.annotation.VisibleForTesting

const val START_ACTIVITY_PATH = "/start-activity"
const val DAY_COFFEE_PATH = "/coffee"

@VisibleForTesting
fun Map<CoffeeType, Int>.withEmpty(): List<Pair<CoffeeType, Int>> {
    @Suppress("DataClassShouldBeImmutable")
    data class MutablePair(val ct: CoffeeType, var count: Int)

    val emptyList: MutableList<MutablePair> =
        CoffeeType.entries.map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { it.ct to it.count }
}
