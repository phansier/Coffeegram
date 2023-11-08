package ru.beryukhov.coffeegram.utils

import org.threeten.bp.YearMonth

fun YearMonth.toTotalMonths() : Int {
    val yearsInMonths = this.year * 12
    val months = this.monthValue + 1
    return yearsInMonths + months
}

fun Int.toYearMonth() : YearMonth {
    val years = this / 12
    val months = this % 12 + 1
    return YearMonth.of(years, months)
}
