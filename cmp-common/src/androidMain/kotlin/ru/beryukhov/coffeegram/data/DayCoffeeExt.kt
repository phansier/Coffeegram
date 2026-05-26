package ru.beryukhov.coffeegram.data

import com.google.android.gms.wearable.DataMap

fun DayCoffee.toDataMap(map: DataMap) {
    map.putIntegerArrayList(KEY, toCountsList())
}

fun DayCoffee.toCountsList(): ArrayList<Int> =
    ArrayList(CoffeeTypes.entries.map { coffeeCountMap[it] ?: 0 })

fun ArrayList<Int>.toDayCoffee(): DayCoffee {
    val entries = CoffeeTypes.entries
    val n = minOf(size, entries.size)
    val m = mutableMapOf<CoffeeType, Int>()
    for (i in 0 until n) {
        m[entries[i]] = this[i]
    }
    return DayCoffee(coffeeCountMap = m)
}

const val KEY = "coffeeCountMap"
