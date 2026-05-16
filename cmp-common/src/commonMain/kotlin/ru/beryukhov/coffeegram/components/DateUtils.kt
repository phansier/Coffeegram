package ru.beryukhov.coffeegram.components

import androidx.compose.runtime.Composable
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.day_fri
import coffeegram.cmp_common.generated.resources.day_mon
import coffeegram.cmp_common.generated.resources.day_sat
import coffeegram.cmp_common.generated.resources.day_sun
import coffeegram.cmp_common.generated.resources.day_thu
import coffeegram.cmp_common.generated.resources.day_tue
import coffeegram.cmp_common.generated.resources.day_wed
import coffeegram.cmp_common.generated.resources.month_apr
import coffeegram.cmp_common.generated.resources.month_aug
import coffeegram.cmp_common.generated.resources.month_dec
import coffeegram.cmp_common.generated.resources.month_feb
import coffeegram.cmp_common.generated.resources.month_jan
import coffeegram.cmp_common.generated.resources.month_jul
import coffeegram.cmp_common.generated.resources.month_jun
import coffeegram.cmp_common.generated.resources.month_mar
import coffeegram.cmp_common.generated.resources.month_may
import coffeegram.cmp_common.generated.resources.month_nov
import coffeegram.cmp_common.generated.resources.month_oct
import coffeegram.cmp_common.generated.resources.month_sep
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource

val dayNames@Composable get() = listOf(
    stringResource(Res.string.day_mon),
    stringResource(Res.string.day_tue),
    stringResource(Res.string.day_wed),
    stringResource(Res.string.day_thu),
    stringResource(Res.string.day_fri),
    stringResource(Res.string.day_sat),
    stringResource(Res.string.day_sun),
)

@Composable
fun getFullMonthName(month: Month): String {
    return when (month) {
        Month.JANUARY -> stringResource(Res.string.month_jan)
        Month.FEBRUARY -> stringResource(Res.string.month_feb)
        Month.MARCH -> stringResource(Res.string.month_mar)
        Month.APRIL -> stringResource(Res.string.month_apr)
        Month.MAY -> stringResource(Res.string.month_may)
        Month.JUNE -> stringResource(Res.string.month_jun)
        Month.JULY -> stringResource(Res.string.month_jul)
        Month.AUGUST -> stringResource(Res.string.month_aug)
        Month.SEPTEMBER -> stringResource(Res.string.month_sep)
        Month.OCTOBER -> stringResource(Res.string.month_oct)
        Month.NOVEMBER -> stringResource(Res.string.month_nov)
        Month.DECEMBER -> stringResource(Res.string.month_dec)
    }
}
