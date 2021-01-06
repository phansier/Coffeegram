package ru.beryukhov.coffeegram

import java.time.LocalDate

actual typealias LocalDate = LocalDate

actual fun now() = LocalDate.now()