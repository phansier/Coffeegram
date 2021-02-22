package ru.beryukhov.coffeegram.date_time.year_month

import androidx.compose.ui.text.intl.Locale
import ru.beryukhov.coffeegram.date_time.local_date.LocalDate

expect class YearMonth

expect fun now(): YearMonth

expect val YearMonth.year: Int
expect val YearMonth.monthValue: Int

expect fun YearMonth.plusMonths(monthsToAdd: Long):YearMonth
expect fun YearMonth.minusMonths(monthsToAdd: Long):YearMonth
expect fun YearMonth.isValidDay(dayOfMonth: Int):Boolean
expect fun YearMonth.atDay(dayOfMonth: Int): LocalDate

expect fun YearMonth.getFullMonthName(locale: Locale): String

expect fun of(year: Int, month: Int): YearMonth

expect enum class DayOfWeek
expect fun DayOfWeek.getShortDisplayName(locale: Locale): String
