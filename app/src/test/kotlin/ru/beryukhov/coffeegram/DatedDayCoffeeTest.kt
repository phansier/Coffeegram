package ru.beryukhov.coffeegram

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ru.beryukhov.coffeegram.data.CoffeeTypes.Latte
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.currentDateFlow
import ru.beryukhov.coffeegram.data.dayCoffeeOn
import ru.beryukhov.coffeegram.data.parseDate
import ru.beryukhov.coffeegram.data.updatedBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

class DatedDayCoffeeTest {

    private val yesterday = LocalDate(2026, 11, 15)
    private val today = LocalDate(2026, 11, 16)
    private val twoLattes = DayCoffee(mapOf(Latte to 2))

    @Test
    fun countsAreShownOnTheirOwnDay() {
        assertEquals(twoLattes, DatedDayCoffee(today, twoLattes).dayCoffeeOn(today))
    }

    @Test
    fun countsFromPreviousDayAreShownAsEmpty() {
        assertEquals(DayCoffee(), DatedDayCoffee(yesterday, twoLattes).dayCoffeeOn(today))
    }

    @Test
    fun olderSnapshotDoesNotUpdateNewerOne() {
        val current = DatedDayCoffee(today, twoLattes)
        val stale = DatedDayCoffee(yesterday, DayCoffee(mapOf(Latte to 5)))
        assertEquals(current, current.updatedBy(stale))
    }

    @Test
    fun sameOrNewerDaySnapshotUpdatesCurrent() {
        val current = DatedDayCoffee(today, twoLattes)
        val sameDay = DatedDayCoffee(today, DayCoffee(mapOf(Latte to 3)))
        assertEquals(sameDay, current.updatedBy(sameDay))
        assertEquals(current, DatedDayCoffee(yesterday).updatedBy(current))
    }

    @Test
    fun parsesIsoDateAndRejectsGarbage() {
        assertEquals(today, parseDate("2026-11-16"))
        assertNull(parseDate("not-a-date"))
        assertNull(parseDate(null))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currentDateFlowEmitsNextDayAfterMidnight() = runTest {
        val start = LocalDateTime(2026, 11, 16, 23, 58).toInstant(TimeZone.UTC)
        val dates = currentDateFlow(clock = virtualClock(start), timeZone = TimeZone.UTC)
            .take(2)
            .toList()
        assertEquals(listOf(today, LocalDate(2026, 11, 17)), dates)
    }

    private fun TestScope.virtualClock(start: Instant) = object : Clock {
        override fun now(): Instant = start + currentTime.milliseconds
    }
}
