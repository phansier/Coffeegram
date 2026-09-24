package ru.beryukhov.coffeegram.pages

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.screens.OverTimeBucket
import ru.beryukhov.coffeegram.screens.OverTimeScale
import ru.beryukhov.coffeegram.screens.OverTimeSeries
import ru.beryukhov.coffeegram.screens.WeeklyChartData
import ru.beryukhov.coffeegram.screens.entries
import ru.beryukhov.coffeegram.screens.overTimeScale
import ru.beryukhov.coffeegram.screens.overTimeSeries
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
    fun testDailyOverTimeSeriesFillsEmptyDays() {
        val actual = overTimeSeries(
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2022, 12, 31) to DayCoffee(mapOf(CoffeeTypes.Irish to 6)),
                    LocalDate(2023, 1, 2) to DayCoffee(mapOf(CoffeeTypes.Latte to 1, CoffeeTypes.Espresso to 2)),
                    LocalDate(2023, 1, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            ),
            today = LocalDate(2023, 1, 4),
        )
        assertEquals(
            expected = OverTimeSeries(
                scale = OverTimeScale.Days,
                buckets = listOf(
                    OverTimeBucket(LocalDate(2022, 12, 31), 6),
                    OverTimeBucket(LocalDate(2023, 1, 1), 0),
                    OverTimeBucket(LocalDate(2023, 1, 2), 3),
                    OverTimeBucket(LocalDate(2023, 1, 3), 4),
                    OverTimeBucket(LocalDate(2023, 1, 4), 0),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testWeeklyOverTimeSeriesGroupsByMonday() {
        val actual = overTimeSeries(
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2023, 1, 4) to DayCoffee(mapOf(CoffeeTypes.Latte to 2)),
                    LocalDate(2023, 1, 8) to DayCoffee(mapOf(CoffeeTypes.Latte to 1)),
                    LocalDate(2023, 1, 23) to DayCoffee(mapOf(CoffeeTypes.Mocha to 5)),
                )
            ),
            today = LocalDate(2023, 2, 20),
        )
        assertEquals(
            expected = OverTimeSeries(
                scale = OverTimeScale.Weeks,
                buckets = listOf(
                    OverTimeBucket(LocalDate(2023, 1, 2), 3),
                    OverTimeBucket(LocalDate(2023, 1, 9), 0),
                    OverTimeBucket(LocalDate(2023, 1, 16), 0),
                    OverTimeBucket(LocalDate(2023, 1, 23), 5),
                    OverTimeBucket(LocalDate(2023, 1, 30), 0),
                    OverTimeBucket(LocalDate(2023, 2, 6), 0),
                    OverTimeBucket(LocalDate(2023, 2, 13), 0),
                    OverTimeBucket(LocalDate(2023, 2, 20), 0),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testMonthlyOverTimeSeriesFillsEmptyMonths() {
        val actual = overTimeSeries(
            coffeeState = DaysCoffeesState(
                coffees = mapOf(
                    LocalDate(2022, 6, 14) to DayCoffee(mapOf(CoffeeTypes.Frappe to 7)),
                    LocalDate(2023, 1, 2) to DayCoffee(mapOf(CoffeeTypes.Latte to 1, CoffeeTypes.Espresso to 2)),
                    LocalDate(2023, 2, 3) to DayCoffee(mapOf(CoffeeTypes.Latte to 4)),
                )
            ),
            today = LocalDate(2023, 2, 10),
        )
        assertEquals(
            expected = OverTimeSeries(
                scale = OverTimeScale.Months,
                buckets = listOf(
                    OverTimeBucket(LocalDate(2022, 6, 1), 7),
                    OverTimeBucket(LocalDate(2022, 7, 1), 0),
                    OverTimeBucket(LocalDate(2022, 8, 1), 0),
                    OverTimeBucket(LocalDate(2022, 9, 1), 0),
                    OverTimeBucket(LocalDate(2022, 10, 1), 0),
                    OverTimeBucket(LocalDate(2022, 11, 1), 0),
                    OverTimeBucket(LocalDate(2022, 12, 1), 0),
                    OverTimeBucket(LocalDate(2023, 1, 1), 3),
                    OverTimeBucket(LocalDate(2023, 2, 1), 4),
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun testOverTimeScaleBoundaries() {
        val first = LocalDate(2023, 1, 1)
        assertEquals(OverTimeScale.Days, overTimeScale(first, LocalDate(2023, 2, 1)))
        assertEquals(OverTimeScale.Weeks, overTimeScale(first, LocalDate(2023, 2, 2)))
        assertEquals(OverTimeScale.Weeks, overTimeScale(first, LocalDate(2023, 7, 2)))
        assertEquals(OverTimeScale.Months, overTimeScale(first, LocalDate(2023, 7, 3)))
    }
}
