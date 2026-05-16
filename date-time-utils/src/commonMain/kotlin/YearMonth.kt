package ru.beryukhov.date_time_utils

import kotlinx.datetime.DateTimeUnit.Companion.MONTH
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class YearMonth(val year: Int, val month: Month) : Comparable<YearMonth> {

    fun plusMonths(monthsToAdd: Int): YearMonth {
        if (monthsToAdd == 0) {
            return this
        }
        val ld = atDay(1)
        val newLd = ld.plus(monthsToAdd, MONTH)
        return YearMonth(newLd.year, newLd.month)
    }

    fun minusMonths(monthsToSubtract: Int): YearMonth {
        return plusMonths(-monthsToSubtract)
    }

    fun atDay(day: Int): LocalDate {
        return LocalDate(year, month, day)
    }

    fun lengthOfMonth(): Int {
        return month.days(isLeapYear(year))
    }

    override fun compareTo(other: YearMonth): Int {
        return toTotalMonths().compareTo(other.toTotalMonths())
    }

    override fun toString(): String {
        return "${month.name.take(3)} $year"
    }
}

fun nowYM(): YearMonth {
    val ld = nowLD()
    return YearMonth(ld.year, ld.month)
}

fun nowLD(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

private fun Month.days(leapYear: Boolean): Int =
    when (this) {
        Month.FEBRUARY -> if (leapYear) 29 else 28
        Month.APRIL,
        Month.JUNE,
        Month.SEPTEMBER,
        Month.NOVEMBER -> 30

        else -> 31
    }

private fun isLeapYear(prolepticYear: Int): Boolean =
    prolepticYear and 3 == 0 && (prolepticYear % 100 != 0 || prolepticYear % 400 == 0)

fun YearMonth.toTotalMonths(): Int {
    val yearsInMonths = this.year * 12
    val months = this.month.number - 1
    return yearsInMonths + months
}

fun Int.toYearMonth(): YearMonth {
    val years = this / 12
    val months = this % 12 + 1
    return YearMonth(years, Month(months))
}
