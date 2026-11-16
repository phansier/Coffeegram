package ru.beryukhov.coffeegram.wear

import com.google.android.gms.wearable.DataMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.currentDate
import ru.beryukhov.coffeegram.data.dayCoffeeOn
import ru.beryukhov.coffeegram.data.updatedBy
import ru.beryukhov.coffeegram.data.toDatedDayCoffee

val coffeeState: MutableStateFlow<DatedDayCoffee> by lazy {
    MutableStateFlow(DatedDayCoffee(date = currentDate()))
}

fun acceptPhoneSnapshot(dataMap: DataMap) {
    val snapshot = dataMap.toDatedDayCoffee(dateWhenMissing = currentDate()) ?: return
    coffeeState.update { current -> current.updatedBy(snapshot) }
}

fun applyDelta(type: CoffeeType, delta: Int): Boolean {
    var changed = false
    coffeeState.update { current ->
        val today = currentDate()
        val counts = current.dayCoffeeOn(today).coffeeCountMap
        val currentCount = counts[type] ?: 0
        val nextCount = (currentCount + delta).coerceAtLeast(0)
        if (nextCount == currentCount) {
            current
        } else {
            changed = true
            DatedDayCoffee(date = today, dayCoffee = DayCoffee(counts + (type to nextCount)))
        }
    }
    return changed
}
