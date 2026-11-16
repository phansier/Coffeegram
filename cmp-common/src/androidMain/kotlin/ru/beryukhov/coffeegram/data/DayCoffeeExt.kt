package ru.beryukhov.coffeegram.data

import com.google.android.gms.wearable.DataMap
import kotlinx.datetime.LocalDate

const val KEY = "coffeeCountMap"
const val DATE_KEY = "date"

fun DatedDayCoffee.toDataMap(map: DataMap) {
    map.putIntegerArrayList(KEY, dayCoffee.toCountsList())
    map.putString(DATE_KEY, date.toString())
}

fun DataMap.toDatedDayCoffee(dateWhenMissing: LocalDate): DatedDayCoffee? {
    val counts = getIntegerArrayList(KEY) ?: return null
    return DatedDayCoffee(
        date = parseDate(getString(DATE_KEY)) ?: dateWhenMissing,
        dayCoffee = counts.toDayCoffee(),
    )
}

fun parseDate(isoDate: String?): LocalDate? =
    isoDate?.let { LocalDate.Formats.ISO.parseOrNull(it) }

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
