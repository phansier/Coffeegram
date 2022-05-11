package ru.beryukhov.coffeegram

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate
import repository.model.DbDayCoffee
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeType.Americano
import ru.beryukhov.coffeegram.data.CoffeeType.Cappuccino
import ru.beryukhov.coffeegram.data.CoffeeType.Latte
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.changeCoffeeCount
import ru.beryukhov.coffeegram.pages.withEmpty
import ru.beryukhov.coffeegram.repository.toDaysCoffeesList
import ru.beryukhov.coffeegram.repository.toState

class DataMappingTest {

    private val exampleDaysCoffeesState = DaysCoffeesState(
        mapOf(
            LocalDate.of(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 0
                )
            ),
            LocalDate.of(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate.of(2022, 10, 23) to DayCoffee(
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
        val actual = exampleDaysCoffeesState.value.toDaysCoffeesList()
        assertEquals(exampleDbDayCoffeeList, actual)
    }

    @Test
    fun parseDate() {
        assertEquals(LocalDate.of(2021, 8, 15), LocalDate.parse("2021-08-15"))
    }

    @Test
    fun toListAndBack() {
        val actual: DaysCoffeesState = exampleDaysCoffeesState.value.toDaysCoffeesList().toState()
        assertEquals(exampleDaysCoffeesState, actual)
    }

    @Test
    fun toStateAndBack() {
        val actual: List<DbDayCoffee> = exampleDbDayCoffeeList.toState().value.toDaysCoffeesList()
        assertEquals(exampleDbDayCoffeeList, actual)
    }

    @Test
    fun changeCoffeeCountToAbsentDateTest() {
        val actual: Map<LocalDate, DayCoffee> = changeCoffeeCount(
            oldValue = exampleDaysCoffeesState.value,
            localDate = LocalDate.of(2021, 8, 14),
            coffeeType = Cappuccino,
            count = 1
        )
        val expected = mapOf(
            LocalDate.of(2021, 8, 14) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                )
            ),
            LocalDate.of(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 0,
                )
            ),
            LocalDate.of(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate.of(2022, 10, 23) to DayCoffee(
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
            oldValue = exampleDaysCoffeesState.value,
            localDate = LocalDate.of(2021, 8, 15),
            coffeeType = Cappuccino,
            count = 1
        )
        val expected = mapOf(
            LocalDate.of(2021, 8, 15) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                )
            ),
            LocalDate.of(2021, 9, 22) to DayCoffee(
                mapOf(
                    Cappuccino to 1,
                    Latte to 2
                )
            ),
            LocalDate.of(2022, 10, 23) to DayCoffee(
                mapOf(
                    Cappuccino to 3
                )
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun withEmptyTest() {
        val map = mapOf<CoffeeType, Int>(
            Cappuccino to 2,
            Latte to 3
        )
        val actual = map.withEmpty()
        val expected = listOf(
            Cappuccino to 2,
            Latte to 3,
            Americano to 0
        )
        assertEquals(expected, actual)
    }
}
