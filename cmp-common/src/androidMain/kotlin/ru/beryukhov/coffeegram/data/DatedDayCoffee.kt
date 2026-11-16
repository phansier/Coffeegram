package ru.beryukhov.coffeegram.data

import kotlinx.datetime.LocalDate

data class DatedDayCoffee(val date: LocalDate, val dayCoffee: DayCoffee = DayCoffee())

fun DatedDayCoffee.dayCoffeeOn(today: LocalDate): DayCoffee =
    if (date == today) dayCoffee else DayCoffee()

fun DatedDayCoffee.updatedBy(other: DatedDayCoffee): DatedDayCoffee =
    if (other.date >= date) other else this
