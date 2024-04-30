package ru.beryukhov.date_time_utils

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
}
