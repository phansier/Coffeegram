package ru.beryukhov.coffeegram.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionMethodsKtTest {

    @Test
    fun checkConversionAndBack() {
        val totalMonths = 24_287
        val ym = totalMonths.toYearMonth()
        val tm = ym.toTotalMonths()

        assertEquals(totalMonths, tm)
    }
}
