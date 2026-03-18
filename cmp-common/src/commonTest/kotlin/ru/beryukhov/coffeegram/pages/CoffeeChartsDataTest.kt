package ru.beryukhov.coffeegram.pages

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import kotlin.test.Test
import kotlin.test.assertEquals

class CoffeeChartsDataTest {

    @Test
    fun testWeeklyChartData() {
        val actualData = weeklyChartData(
            startOfWeek = LocalDate(2023, 1, 2),
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2023, 1, 2) to DayCoffee(mapOf(CoffeeTypes.Latte to 1, CoffeeTypes.Espresso to 2)),
                    LocalDate(2023, 1, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            )
        )
        assertEquals(
            expected = listOf(
                WeeklyChartData(date = LocalDate(2023, 1, 2), dayName = "Mon", totalCoffees = 3),
                WeeklyChartData(date = LocalDate(2023, 1, 3), dayName = "Tue", totalCoffees = 4),
                WeeklyChartData(date = LocalDate(2023, 1, 4), dayName = "Wed", totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 5), dayName = "Thu", totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 6), dayName = "Fri", totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 7), dayName = "Sat", totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 8), dayName = "Sun", totalCoffees = 0),

                ),
            actual = actualData
        )

        val actualFloatEntries = entries(actualData)
        assertEquals(
            expected = (0..6).toList() to listOf(3, 4, 0, 0, 0, 0, 0),
            actual = actualFloatEntries
        )
    }

    @Test
    fun testDailyAggregation() {
        val actualData = dailyAggregation(
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2022, 12, 31) to DayCoffee(mapOf(CoffeeTypes.Irish to 6)),
                    LocalDate(2023, 1, 2) to DayCoffee(mapOf(CoffeeTypes.Latte to 1, CoffeeTypes.Espresso to 2)),
                    LocalDate(2023, 1, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            )
        )
        assertEquals(
            expected = listOf(
                AggregatedData(label = "DEC 31", totalCount = 6),
                AggregatedData(label = "JAN 2", totalCount = 3),
                AggregatedData(label = "JAN 3", totalCount = 4),
            ),
            actual = actualData
        )
    }

    @Test
    fun testMonthlyAggregation() {
        val actualData = monthlyAggregation(
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2022, 8, 14) to DayCoffee(mapOf(CoffeeTypes.Frappe to 7)),
                    LocalDate(2023, 1, 2) to DayCoffee(mapOf(CoffeeTypes.Latte to 1, CoffeeTypes.Espresso to 2)),
                    LocalDate(2023, 2, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            )
        )
        assertEquals(
            expected = listOf(
                AggregatedData(label = "AUG 2022", totalCount = 7),
                AggregatedData(label = "JAN 2023", totalCount = 3),
                AggregatedData(label = "FEB 2023", totalCount = 4),
            ),
            actual = actualData
        )
    }
}
