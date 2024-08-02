package ru.beryukhov.date_time_utils

import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionMethodsKtTest {

    @Test
    fun checkConversionAndBack() {
        val totalMonths = 24_287
        val ym = totalMonths.toYearMonth()
        val tm = ym.toTotalMonths()

        assertEquals(totalMonths, tm)
    }

    @Test
    fun checkReversedConversionAndBack() {
        val ym = YearMonth(2020, Month(1))
        val tm = ym.toTotalMonths()
        val newym = tm.toYearMonth()

        assertEquals(ym, newym)
    }

    @Test
    fun checkReversedConversionAndBack9() {
        val ym = YearMonth(2020, Month(9))
        val tm = ym.toTotalMonths()
        val newym = tm.toYearMonth()

        assertEquals(ym, newym)
    }
}
