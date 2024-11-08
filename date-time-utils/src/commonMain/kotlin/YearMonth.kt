package ru.beryukhov.date_time_utils

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit.Companion.MONTH
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class YearMonth(val year: Int, val month: Month) {

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

    fun isValidDay(dayOfMonth: Int): Boolean {
        return dayOfMonth >= 1 && dayOfMonth <= lengthOfMonth()
    }

    private fun lengthOfMonth(): Int {
        return month.days(isLeapYear(year))
    }
}

fun getFullMonthName(month: Month): String =
    when (month) {
        Month.JANUARY -> "JANUARY"
        Month.FEBRUARY -> "FEBRUARY"
        Month.MARCH -> "MARCH"
        Month.APRIL -> "APRIL"
        Month.MAY -> "MAY"
        Month.JUNE -> "JUNE"
        Month.JULY -> "JULY"
        Month.AUGUST -> "AUGUST"
        Month.SEPTEMBER -> "SEPTEMBER"
        Month.OCTOBER -> "OCTOBER"
        Month.NOVEMBER -> "NOVEMBER"
        Month.DECEMBER -> "DECEMBER"
        else -> ""
    }.lowercase().replaceFirstChar { it.titlecase() }

fun nowYM(): YearMonth {
    val ld = nowLD()
    return YearMonth(ld.year, ld.month)
}

fun nowLD(): LocalDate {
    return now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun DayOfWeek.getShortDisplayName(): String =
    when (this) {
        DayOfWeek.MONDAY -> "MON"
        DayOfWeek.TUESDAY -> "TUE"
        DayOfWeek.WEDNESDAY -> "WED"
        DayOfWeek.THURSDAY -> "THU"
        DayOfWeek.FRIDAY -> "FRI"
        DayOfWeek.SATURDAY -> "SAT"
        DayOfWeek.SUNDAY -> "SUN"
        else -> ""
    }.lowercase().replaceFirstChar { it.titlecase() }

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

fun dateFormatSymbolsShortWeekdays(): List<String> =
    DayOfWeek.values().map { it.getShortDisplayName() }

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
