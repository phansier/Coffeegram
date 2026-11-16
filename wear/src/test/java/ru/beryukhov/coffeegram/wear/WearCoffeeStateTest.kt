package ru.beryukhov.coffeegram.wear

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeChangeEvent
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cappuccino
import ru.beryukhov.coffeegram.data.CoffeeTypes.Latte
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import kotlin.test.Test
import kotlin.test.assertEquals

class WearCoffeeStateTest {

    private val yesterday = LocalDate(2026, 11, 15)
    private val today = LocalDate(2026, 11, 16)

    private val addLatte = CoffeeChangeEvent(today, Latte, +1)

    private fun phone(latte: Int, cappuccino: Int = 0) =
        DatedDayCoffee(today, DayCoffee(mapOf(Latte to latte, Cappuccino to cappuccino)))

    private fun latteOn(state: WearCoffeeState) = state.dayCoffeeOn(today).coffeeCountMap[Latte]

    @Test
    fun unconfirmedChangesFromOutboxAreShownOnTopOfPhoneCounts() {
        val restarted = WearCoffeeState(phone(latte = 6))
            .withUnconfirmedChanges(mapOf("/coffee-event/a" to addLatte, "/coffee-event/b" to addLatte))
        assertEquals(8, latteOn(restarted))
    }

    @Test
    fun phoneEchoesDoNotChangeShownCountWhileChangesArePending() {
        var state = WearCoffeeState(phone(latte = 8))
            .withLocalChange("a", addLatte)
            .withLocalChange("b", addLatte)
            .withLocalChange("c", addLatte)
        assertEquals(11, latteOn(state))

        state = state.withPhoneSnapshot(phone(latte = 9)).withConfirmedChange("a")
        assertEquals(11, latteOn(state))
        state = state.withPhoneSnapshot(phone(latte = 10)).withConfirmedChange("b")
        assertEquals(11, latteOn(state))
        state = state.withPhoneSnapshot(phone(latte = 11)).withConfirmedChange("c")
        assertEquals(11, latteOn(state))
    }

    @Test
    fun phoneSideChangesAppearOnceEverythingIsConfirmed() {
        val state = WearCoffeeState(phone(latte = 1))
            .withLocalChange("a", addLatte)
            .withPhoneSnapshot(phone(latte = 2, cappuccino = 3))
        assertEquals(0, state.dayCoffeeOn(today).coffeeCountMap[Cappuccino])

        val settled = state.withConfirmedChange("a")
        assertEquals(DayCoffee(mapOf(Latte to 2, Cappuccino to 3)), settled.dayCoffeeOn(today))
    }

    @Test
    fun phoneCountsAreShownImmediatelyWhenNothingIsPending() {
        val state = WearCoffeeState(phone(latte = 1)).withPhoneSnapshot(phone(latte = 4))
        assertEquals(4, latteOn(state))
    }

    @Test
    fun alreadyConfirmedChangeIsNotRestoredFromAStaleOutboxRead() {
        val state = WearCoffeeState(phone(latte = 1))
            .withLocalChange("a", addLatte)
            .withPhoneSnapshot(phone(latte = 2))
            .withConfirmedChange("a")
            .withUnconfirmedChanges(mapOf("a" to addLatte))
        assertEquals(2, latteOn(state))
    }

    @Test
    fun failedSendIsRolledBack() {
        val state = WearCoffeeState(phone(latte = 1))
            .withLocalChange("a", addLatte)
            .withoutLocalChange("a")
        assertEquals(1, latteOn(state))
    }

    @Test
    fun changesFromPreviousDayAreNotCountedToday() {
        val state = WearCoffeeState(phone(latte = 1))
            .withUnconfirmedChanges(mapOf("a" to CoffeeChangeEvent(yesterday, Latte, +1)))
        assertEquals(1, latteOn(state))
    }

    @Test
    fun countNeverGoesBelowZero() {
        val state = WearCoffeeState(phone(latte = 0))
            .withUnconfirmedChanges(mapOf("a" to CoffeeChangeEvent(today, Latte, -1)))
        assertEquals(0, latteOn(state))
    }
}
