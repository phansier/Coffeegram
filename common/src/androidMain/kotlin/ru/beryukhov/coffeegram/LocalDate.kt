package ru.beryukhov.coffeegram

actual typealias LocalDate = org.threeten.bp.LocalDate

actual fun now() = LocalDate.now()