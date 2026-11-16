package ru.beryukhov.coffeegram.data

import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.datetime.LocalDate

data class CoffeeChangeEvent(val date: LocalDate, val type: CoffeeType, val delta: Int)

fun CoffeeChangeEvent.toPutDataRequest(eventPath: String): PutDataRequest =
    PutDataMapRequest.create(eventPath).apply {
        dataMap.putString(COFFEE_EVENT_KEY_DB_KEY, type.dbKey)
        dataMap.putInt(COFFEE_EVENT_KEY_DELTA, delta)
        dataMap.putString(COFFEE_EVENT_KEY_DATE, date.toString())
    }.asPutDataRequest().setUrgent()

fun DataMap.toCoffeeChangeEvent(dateWhenMissing: LocalDate): CoffeeChangeEvent? {
    val type = CoffeeTypes.entries.find { it.dbKey == getString(COFFEE_EVENT_KEY_DB_KEY) } ?: return null
    val delta = getInt(COFFEE_EVENT_KEY_DELTA, 0).takeIf { it != 0 } ?: return null
    return CoffeeChangeEvent(
        date = parseDate(getString(COFFEE_EVENT_KEY_DATE)) ?: dateWhenMissing,
        type = type,
        delta = delta,
    )
}
