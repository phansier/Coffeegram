package ru.beryukhov.coffeegram.model

import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.coffeeTypeValues

private class MutablePair(val ct: CoffeeType, var count: Int)

// @VisibleForTesting
internal fun Map<CoffeeType, Int>.withEmpty(): List<CoffeeTypeWithCount> {
    val emptyList: MutableList<MutablePair> =
        coffeeTypeValues().toList().map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { CoffeeTypeWithCount(it.ct, it.count) }
}
