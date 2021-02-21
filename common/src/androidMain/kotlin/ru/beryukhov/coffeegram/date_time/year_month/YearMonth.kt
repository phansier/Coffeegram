package ru.beryukhov.coffeegram.date_time.year_month

import androidx.compose.ui.text.intl.Locale
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.date_time.local_date.LocalDate

actual typealias YearMonth = org.threeten.bp.YearMonth

actual fun now(): YearMonth = YearMonth.now()


actual val YearMonth.year: Int
    get() = this.year
actual val YearMonth.monthValue: Int
    get() = this.monthValue

actual fun YearMonth.plusMonths(monthsToAdd: Long): YearMonth = plusMonths(monthsToAdd)
actual fun YearMonth.minusMonths(monthsToAdd: Long): YearMonth = minusMonths(monthsToAdd)
actual fun YearMonth.isValidDay(dayOfMonth: Int):Boolean = isValidDay(dayOfMonth)
actual fun YearMonth.atDay(dayOfMonth: Int):LocalDate = atDay(dayOfMonth)

actual fun of(
    year: Int,
    month: Int
) = YearMonth.of(year, month)


actual typealias DayOfWeek = DayOfWeek
actual fun DayOfWeek.getShortDisplayName(locale: Locale) = getDisplayName(TextStyle.SHORT,java.util.Locale(locale.language, locale.region))
