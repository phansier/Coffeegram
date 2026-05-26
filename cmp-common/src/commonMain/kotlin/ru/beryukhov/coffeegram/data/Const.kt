package ru.beryukhov.coffeegram.data

const val START_ACTIVITY_PATH = "/start-activity"
const val DAY_COFFEE_PATH = "/coffee"

// Wear -> phone: each delta is published as its own DataItem under this prefix
// (e.g. "/coffee-event/<uuid>") so that Play Services queues and replays events
// when the phone is offline. The DataMap carries the dbKey and delta below.
const val COFFEE_EVENT_PATH_PREFIX = "/coffee-event/"
const val COFFEE_EVENT_KEY_DB_KEY = "dbKey"
const val COFFEE_EVENT_KEY_DELTA = "delta"

// Capability advertised by the wear app; the phone uses this to detect paired
// watches via CapabilityClient. Must match wear/src/main/res/values/wear.xml.
const val WEAR_CAPABILITY = "coffeegram_wear"

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
