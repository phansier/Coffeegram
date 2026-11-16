package ru.beryukhov.coffeegram.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val MAX_DATE_CHECK_INTERVAL = 1.minutes

fun currentDate(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDate = clock.todayIn(timeZone)

fun currentDateFlow(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Flow<LocalDate> = flow {
    while (true) {
        val today = currentDate(clock, timeZone)
        emit(today)
        delay(minOf(clock.untilDayAfter(today, timeZone), MAX_DATE_CHECK_INTERVAL))
    }
}.distinctUntilChanged()

private fun Clock.untilDayAfter(day: LocalDate, timeZone: TimeZone): Duration =
    day.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone) - now()
