package ru.beryukhov.coffeegram.date_time.year_month

expect class YearMonth
expect fun now(): YearMonth

expect val YearMonth.year: Int
expect val YearMonth.monthValue: Int

expect fun YearMonth.plusMonths(monthsToAdd: Long):YearMonth
expect fun YearMonth.minusMonths(monthsToAdd: Long):YearMonth

expect fun of(year: Int, month: Int): YearMonth
