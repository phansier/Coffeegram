package ru.beryukhov.coffeegram.date_time.local_date

expect class LocalDate

expect fun now(): LocalDate

expect val LocalDate.year: Int
expect val LocalDate.monthValue: Int

expect fun of(year: Int, month: Int, dayOfMonth: Int): LocalDate

