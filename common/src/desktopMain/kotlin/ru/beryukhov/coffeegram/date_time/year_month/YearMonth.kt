package ru.beryukhov.coffeegram.date_time.year_month

import androidx.compose.ui.text.intl.Locale
import ru.beryukhov.coffeegram.date_time.local_date.LocalDate
import java.time.format.TextStyle


actual typealias YearMonth = java.time.YearMonth

actual fun now(): YearMonth = java.time.YearMonth.now()

actual val YearMonth.year: Int
    get() = this.year
actual val YearMonth.monthValue: Int
    get() = this.monthValue

actual fun YearMonth.plusMonths(monthsToAdd: Long):YearMonth = plusMonths(monthsToAdd)
actual fun YearMonth.minusMonths(monthsToAdd: Long):YearMonth = minusMonths(monthsToAdd)
actual fun YearMonth.isValidDay(dayOfMonth: Int):Boolean = isValidDay(dayOfMonth)
actual fun YearMonth.atDay(dayOfMonth: Int):LocalDate = atDay(dayOfMonth)

actual fun YearMonth.getFullMonthName(locale: Locale): String = month.getDisplayName(TextStyle.FULL, java.util.Locale(locale.language, locale.region))

actual fun of(
    year: Int,
    month: Int
) = java.time.YearMonth.of(year, month)

actual typealias DayOfWeek = java.time.DayOfWeek

actual fun DayOfWeek.getShortDisplayName(locale: Locale) = getDisplayName(TextStyle.SHORT,java.util.Locale(locale.language, locale.region))