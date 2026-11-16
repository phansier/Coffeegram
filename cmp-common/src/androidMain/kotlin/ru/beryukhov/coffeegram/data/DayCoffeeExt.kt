package ru.beryukhov.coffeegram.data

import com.google.android.gms.wearable.DataMap
import kotlinx.datetime.LocalDate

const val COUNTS_BY_DB_KEY = "countsByDbKey"
const val LEGACY_POSITIONAL_COUNTS_KEY = "coffeeCountMap"
const val DATE_KEY = "date"

fun DatedDayCoffee.toDataMap(map: DataMap) {
    map.putDataMap(COUNTS_BY_DB_KEY, dayCoffee.toCountsByDbKey().toDataMap())
    map.putIntegerArrayList(LEGACY_POSITIONAL_COUNTS_KEY, dayCoffee.toCountsList())
    map.putString(DATE_KEY, date.toString())
}

fun DataMap.toDatedDayCoffee(dateWhenMissing: LocalDate): DatedDayCoffee? {
    val dayCoffee = getDataMap(COUNTS_BY_DB_KEY)?.toCountsByDbKey()?.toDayCoffee()
        ?: getIntegerArrayList(LEGACY_POSITIONAL_COUNTS_KEY)?.toDayCoffee()
        ?: return null
    return DatedDayCoffee(
        date = parseDate(getString(DATE_KEY)) ?: dateWhenMissing,
        dayCoffee = dayCoffee,
    )
}

fun parseDate(isoDate: String?): LocalDate? =
    isoDate?.let { LocalDate.Formats.ISO.parseOrNull(it) }

fun DayCoffee.toCountsByDbKey(): Map<String, Int> =
    CoffeeTypes.entries.associate { it.dbKey to (coffeeCountMap[it] ?: 0) }

fun Map<String, Int>.toDayCoffee(): DayCoffee =
    DayCoffee(
        coffeeCountMap = CoffeeTypes.entries
            .mapNotNull { type -> this[type.dbKey]?.let { count -> type to count } }
            .toMap()
    )

private fun Map<String, Int>.toDataMap(): DataMap =
    DataMap().also { map -> forEach { (dbKey, count) -> map.putInt(dbKey, count) } }

private fun DataMap.toCountsByDbKey(): Map<String, Int> =
    keySet().associateWith { dbKey -> getInt(dbKey) }

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
