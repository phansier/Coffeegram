package ru.beryukhov.coffeegram

import kotlinx.datetime.LocalDate
import repository.model.DbDayCoffee
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes.Americano
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cappuccino
import ru.beryukhov.coffeegram.data.CoffeeTypes.Chocolate
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cocoa
import ru.beryukhov.coffeegram.data.CoffeeTypes.Espresso
import ru.beryukhov.coffeegram.data.CoffeeTypes.Frappe
import ru.beryukhov.coffeegram.data.CoffeeTypes.Fredo
import ru.beryukhov.coffeegram.data.CoffeeTypes.Glace
import ru.beryukhov.coffeegram.data.CoffeeTypes.Irish
import ru.beryukhov.coffeegram.data.CoffeeTypes.Latte
import ru.beryukhov.coffeegram.data.CoffeeTypes.Macchiato
import ru.beryukhov.coffeegram.data.CoffeeTypes.Mocha
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.toCountsList
import ru.beryukhov.coffeegram.data.toDayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.changeCoffeeCount
import ru.beryukhov.coffeegram.repository.toDaysCoffeesList
import ru.beryukhov.coffeegram.repository.toState
import kotlin.test.Test
import kotlin.test.assertEquals

class DataMappingTest {

    private val exampleDaysCoffeesState = DaysCoffeesState(
        mapOf(
            LocalDate(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 0
                )
            ),
            LocalDate(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate(2022, 10, 23) to DayCoffee(
                mapOf(
                    Cappuccino to 3
                )
            ),
        )
    )

    private val exampleDbDayCoffeeList = listOf(
        DbDayCoffee("2021-08-15", "Cappuccino", 0),
//        DbDayCoffee("2021-08-15", "Latte", 0),
        DbDayCoffee("2021-09-22", "Cappuccino", 1),
        DbDayCoffee("2021-09-22", "Latte", 2),
        DbDayCoffee("2022-10-23", "Cappuccino", 3),
    )

    @Test
    fun toList() {
        val actual = exampleDaysCoffeesState.coffees.toDaysCoffeesList()
        assertEquals(exampleDbDayCoffeeList, actual)
    }

    @Test
    fun parseDate() {
        assertEquals(LocalDate(2021, 8, 15), LocalDate.parse("2021-08-15"))
    }

    @Test
    fun toListAndBack() {
        val actual: DaysCoffeesState = exampleDaysCoffeesState.coffees.toDaysCoffeesList().toState()
        assertEquals(exampleDaysCoffeesState, actual)
    }

    @Test
    fun toStateAndBack() {
        val actual: List<DbDayCoffee> = exampleDbDayCoffeeList.toState().coffees.toDaysCoffeesList()
        assertEquals(exampleDbDayCoffeeList, actual)
    }

    @Test
    fun changeCoffeeCountToAbsentDateTest() {
        val actual: Map<LocalDate, DayCoffee> = changeCoffeeCount(
            oldValue = exampleDaysCoffeesState.coffees,
            localDate = LocalDate(2021, 8, 14),
            coffeeType = Cappuccino,
            count = 1
        )
        val expected = mapOf(
            LocalDate(2021, 8, 14) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                )
            ),
            LocalDate(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 0,
                )
            ),
            LocalDate(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate(2022, 10, 23) to DayCoffee(
                mapOf(
                    Cappuccino to 3
                )
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun changeCoffeeCountToEmptyDateTest() {
        val actual: Map<LocalDate, DayCoffee> = changeCoffeeCount(
            oldValue = exampleDaysCoffeesState.coffees,
            localDate = LocalDate(2021, 8, 15),
            coffeeType = Cappuccino,
            count = 1
        )
        val expected = mapOf(
            LocalDate(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                )
            ),
            LocalDate(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate(2022, 10, 23) to DayCoffee(
                mapOf(
                    Cappuccino to 3
                )
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun wearableEncodingRoundTrip() {
        val original = DayCoffee(
            mapOf(
                Cappuccino to 1,
                Americano to 2,
                Chocolate to 7,
            )
        )
        val roundTripped = original.toCountsList().toDayCoffee()
        for (type in CoffeeTypes.entries) {
            assertEquals(
                original.coffeeCountMap[type] ?: 0,
                roundTripped.coffeeCountMap[type] ?: 0,
                "Mismatch for $type",
            )
        }
    }

    @Test
    fun wearableEncodingIsDeclarationOrdered() {
        val day = DayCoffee(mapOf(Cappuccino to 1, Latte to 2, Americano to 3))
        val counts = day.toCountsList()
        assertEquals(CoffeeTypes.entries.size, counts.size)
        assertEquals(1, counts[CoffeeTypes.entries.indexOf(Cappuccino)])
        assertEquals(2, counts[CoffeeTypes.entries.indexOf(Latte)])
        assertEquals(3, counts[CoffeeTypes.entries.indexOf(Americano)])
    }

    @Test
    fun wearableDecodeToleratesShorterInput() {
        // Older sender, fewer entries — newer receiver shouldn't crash.
        val shortened = ArrayList(listOf(5, 6))
        val decoded = shortened.toDayCoffee()
        assertEquals(5, decoded.coffeeCountMap[CoffeeTypes.entries[0]])
        assertEquals(6, decoded.coffeeCountMap[CoffeeTypes.entries[1]])
        assertEquals(2, decoded.coffeeCountMap.size)
    }

    @Test
    fun withEmptyTest() {
        val map = mapOf<CoffeeType, Int>(
            Cappuccino to 2,
            Latte to 3
        )
        val actual = map.withEmpty()
        val expected = listOf(
            CoffeeTypeWithCount(coffee = Cappuccino, count = 2),
            CoffeeTypeWithCount(coffee = Latte, count = 3),
            CoffeeTypeWithCount(coffee = Americano, count = 0),
            CoffeeTypeWithCount(coffee = Macchiato, count = 0),
            CoffeeTypeWithCount(coffee = Glace, count = 0),
            CoffeeTypeWithCount(coffee = Frappe, count = 0),
            CoffeeTypeWithCount(coffee = Espresso, count = 0),
            CoffeeTypeWithCount(coffee = Mocha, count = 0),
            CoffeeTypeWithCount(coffee = Fredo, count = 0),
            CoffeeTypeWithCount(coffee = Irish, count = 0),
            CoffeeTypeWithCount(coffee = Cocoa, count = 0),
            CoffeeTypeWithCount(coffee = Chocolate, count = 0)
        )
        assertEquals(expected, actual)
    }
}
