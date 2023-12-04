package ru.beryukhov.coffeegram.model

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit.Companion.MONTH
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DayOfWeek.FRIDAY
import kotlinx.datetime.DayOfWeek.MONDAY
import kotlinx.datetime.DayOfWeek.SATURDAY
import kotlinx.datetime.DayOfWeek.SUNDAY
import kotlinx.datetime.DayOfWeek.THURSDAY
import kotlinx.datetime.DayOfWeek.TUESDAY
import kotlinx.datetime.DayOfWeek.WEDNESDAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.Month.APRIL
import kotlinx.datetime.Month.AUGUST
import kotlinx.datetime.Month.DECEMBER
import kotlinx.datetime.Month.FEBRUARY
import kotlinx.datetime.Month.JANUARY
import kotlinx.datetime.Month.JULY
import kotlinx.datetime.Month.JUNE
import kotlinx.datetime.Month.MARCH
import kotlinx.datetime.Month.MAY
import kotlinx.datetime.Month.NOVEMBER
import kotlinx.datetime.Month.OCTOBER
import kotlinx.datetime.Month.SEPTEMBER
import kotlinx.datetime.TimeZone
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

    fun getFullMonthName(): String =
        when (this.month) {
            JANUARY -> "JANUARY"
            FEBRUARY -> "FEBRUARY"
            MARCH -> "MARCH"
            APRIL -> "APRIL"
            MAY -> "MAY"
            JUNE -> "JUNE"
            JULY -> "JULY"
            AUGUST -> "AUGUST"
            SEPTEMBER -> "SEPTEMBER"
            OCTOBER -> "OCTOBER"
            NOVEMBER -> "NOVEMBER"
            DECEMBER -> "DECEMBER"
            else -> ""
        }.lowercase().replaceFirstChar { it.titlecase() }

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

fun nowYM(): YearMonth {
    val ld = nowLD()
    return YearMonth(ld.year, ld.month)
}

fun nowLD(): LocalDate {
    return now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

fun DayOfWeek.getShortDisplayName(): String =
    when (this) {
        MONDAY -> "MON"
        TUESDAY -> "TUE"
        WEDNESDAY -> "WED"
        THURSDAY -> "THU"
        FRIDAY -> "FRI"
        SATURDAY -> "SAT"
        SUNDAY -> "SUN"
        else -> ""
    }.lowercase().replaceFirstChar { it.titlecase() }

private fun Month.days(leapYear: Boolean): Int =
    when (this) {
        FEBRUARY -> if (leapYear) 29 else 28
        APRIL,
        JUNE,
        SEPTEMBER,
        NOVEMBER -> 30
        else -> 31
    }

private fun isLeapYear(prolepticYear: Int): Boolean =
    prolepticYear and 3 == 0 && (prolepticYear % 100 != 0 || prolepticYear % 400 == 0)

fun dateFormatSymbolsShortWeekdays(): List<String> =
    DayOfWeek.values().map { it.getShortDisplayName() }
