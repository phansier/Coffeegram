package ru.beryukhov.coffeegram

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeChangeEvent
import ru.beryukhov.coffeegram.data.CoffeeTypes.Latte
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.wearable.toDaysCoffeesIntent
import kotlin.test.Test
import kotlin.test.assertEquals

class CoffeeChangeIntentTest {

    private val yesterday = LocalDate(2026, 11, 15)

    @Test
    fun watchChangeIsBookedOnTheDayItWasMade() {
        assertEquals(
            DaysCoffeesIntent.PlusCoffee(yesterday, Latte),
            CoffeeChangeEvent(date = yesterday, type = Latte, delta = +1).toDaysCoffeesIntent(),
        )
        assertEquals(
            DaysCoffeesIntent.MinusCoffee(yesterday, Latte),
            CoffeeChangeEvent(date = yesterday, type = Latte, delta = -1).toDaysCoffeesIntent(),
        )
    }
}
