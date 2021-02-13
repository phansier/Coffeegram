package ru.beryukhov.coffeegram.date_time.local_date

import java.time.LocalDate

actual typealias LocalDate = LocalDate

actual fun now() = LocalDate.now()
actual fun of(year: Int, month: Int, dayOfMonth: Int) = LocalDate.of(year, month, dayOfMonth)

actual val LocalDate.year: Int
    get() = this.year
actual val LocalDate.monthValue: Int
    get() = this.monthValue
