package ru.beryukhov.coffeegram.pages

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.screens.AggregatedData
import ru.beryukhov.coffeegram.screens.WeeklyChartData
import ru.beryukhov.coffeegram.screens.dailyAggregation
import ru.beryukhov.coffeegram.screens.entries
import ru.beryukhov.coffeegram.screens.monthlyAggregation
import ru.beryukhov.coffeegram.screens.weeklyChartData
import kotlin.test.Test
import kotlin.test.assertEquals

class CoffeeChartsDataTest {

    @Test
    fun testWeeklyChartData() {
        val actualData = weeklyChartData(
            startOfWeek = LocalDate(2023, 1, 2),
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2023, 1, 2) to DayCoffee(
                        mapOf(
                            CoffeeTypes.Latte to 1,
                            CoffeeTypes.Espresso to 2
                        )
                    ),
                    LocalDate(2023, 1, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            )
        )
        assertEquals(
            expected = listOf(
                WeeklyChartData(date = LocalDate(2023, 1, 2), dayIndex = 0, totalCoffees = 3),
                WeeklyChartData(date = LocalDate(2023, 1, 3), dayIndex = 1, totalCoffees = 4),
                WeeklyChartData(date = LocalDate(2023, 1, 4), dayIndex = 2, totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 5), dayIndex = 3, totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 6), dayIndex = 4, totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 7), dayIndex = 5, totalCoffees = 0),
                WeeklyChartData(date = LocalDate(2023, 1, 8), dayIndex = 6, totalCoffees = 0),
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
                    LocalDate(2023, 1, 2) to DayCoffee(
                        mapOf(
                            CoffeeTypes.Latte to 1,
                            CoffeeTypes.Espresso to 2
                        )
                    ),
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
                    LocalDate(2023, 1, 2) to DayCoffee(
                        mapOf(
                            CoffeeTypes.Latte to 1,
                            CoffeeTypes.Espresso to 2
                        )
                    ),
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
