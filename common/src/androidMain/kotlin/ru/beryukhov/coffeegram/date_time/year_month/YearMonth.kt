package ru.beryukhov.coffeegram.date_time.year_month

actual typealias YearMonth = org.threeten.bp.YearMonth

actual fun now(): YearMonth = YearMonth.now()


actual val YearMonth.year: Int
    get() = this.year
actual val YearMonth.monthValue: Int
    get() = this.monthValue

actual fun YearMonth.plusMonths(monthsToAdd: Long): YearMonth = plusMonths(monthsToAdd)
actual fun YearMonth.minusMonths(monthsToAdd: Long): YearMonth = minusMonths(monthsToAdd)

actual fun of(
    year: Int,
    month: Int
) = YearMonth.of(year, month)