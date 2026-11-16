package ru.beryukhov.coffeegram.wear

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeChangeEvent
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.dayCoffeeOn
import ru.beryukhov.coffeegram.data.updatedBy

data class WearCoffeeState(
    val latestPhoneSnapshot: DatedDayCoffee,
    val shownPhoneSnapshot: DatedDayCoffee = latestPhoneSnapshot,
    val changesSinceShownSnapshot: Map<String, CoffeeChangeEvent> = emptyMap(),
    val confirmedEventPaths: Set<String> = emptySet(),
)

fun WearCoffeeState.dayCoffeeOn(today: LocalDate): DayCoffee =
    changesSinceShownSnapshot.values
        .filter { it.date == today }
        .fold(shownPhoneSnapshot.dayCoffeeOn(today)) { day, change -> day.plus(change.type, change.delta) }

fun WearCoffeeState.withPhoneSnapshot(snapshot: DatedDayCoffee): WearCoffeeState =
    copy(latestPhoneSnapshot = latestPhoneSnapshot.updatedBy(snapshot)).showingLatestWhenSettled()

fun WearCoffeeState.withLocalChange(eventPath: String, change: CoffeeChangeEvent): WearCoffeeState =
    copy(changesSinceShownSnapshot = changesSinceShownSnapshot + (eventPath to change))

fun WearCoffeeState.withoutLocalChange(eventPath: String): WearCoffeeState =
    copy(changesSinceShownSnapshot = changesSinceShownSnapshot - eventPath).showingLatestWhenSettled()

fun WearCoffeeState.withConfirmedChange(eventPath: String): WearCoffeeState =
    copy(confirmedEventPaths = confirmedEventPaths + eventPath).showingLatestWhenSettled()

fun WearCoffeeState.withUnconfirmedChanges(changes: Map<String, CoffeeChangeEvent>): WearCoffeeState =
    copy(changesSinceShownSnapshot = changesSinceShownSnapshot + changes.filterKeys { it !in confirmedEventPaths })

private val WearCoffeeState.isSettled: Boolean
    get() = changesSinceShownSnapshot.keys.all { it in confirmedEventPaths }

private fun WearCoffeeState.showingLatestWhenSettled(): WearCoffeeState =
    if (isSettled) {
        copy(shownPhoneSnapshot = latestPhoneSnapshot, changesSinceShownSnapshot = emptyMap())
    } else {
        this
    }

private fun DayCoffee.plus(type: CoffeeType, delta: Int): DayCoffee {
    val count = ((coffeeCountMap[type] ?: 0) + delta).coerceAtLeast(0)
    return DayCoffee(coffeeCountMap + (type to count))
}
