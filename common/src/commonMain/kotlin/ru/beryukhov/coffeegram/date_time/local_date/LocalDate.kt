package ru.beryukhov.coffeegram.date_time.local_date

import ru.beryukhov.coffeegram.date_time.year_month.DayOfWeek

expect class LocalDate

expect fun now(): LocalDate

expect val LocalDate.year: Int
expect val LocalDate.monthValue: Int
expect val LocalDate.dayOfMonth: Int

expect fun of(year: Int, month: Int, dayOfMonth: Int): LocalDate

expect fun LocalDate.dayOfWeek(): DayOfWeek
