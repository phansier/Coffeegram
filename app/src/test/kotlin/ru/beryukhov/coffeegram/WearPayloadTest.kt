package ru.beryukhov.coffeegram

import com.google.android.gms.wearable.DataMap
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.COUNTS_BY_DB_KEY
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.CoffeeTypes.Americano
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cappuccino
import ru.beryukhov.coffeegram.data.CoffeeTypes.Latte
import ru.beryukhov.coffeegram.data.DATE_KEY
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.LEGACY_POSITIONAL_COUNTS_KEY
import ru.beryukhov.coffeegram.data.toCountsByDbKey
import ru.beryukhov.coffeegram.data.toCountsList
import ru.beryukhov.coffeegram.data.toDataMap
import ru.beryukhov.coffeegram.data.toDatedDayCoffee
import ru.beryukhov.coffeegram.data.toDayCoffee
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WearPayloadTest {

    private val today = LocalDate(2026, 11, 16)
    private val fallbackDate = LocalDate(2000, 1, 1)
    private val day = DayCoffee(mapOf(Cappuccino to 2, Latte to 1, Americano to 3))

    @Test
    fun countsAreKeyedByDbKey() {
        val counts = day.toCountsByDbKey()
        assertEquals(CoffeeTypes.entries.size, counts.size)
        assertEquals(2, counts[Cappuccino.dbKey])
        assertEquals(1, counts[Latte.dbKey])
        assertEquals(3, counts[Americano.dbKey])
    }

    @Test
    fun decodingByDbKeyIgnoresOrderAndUnknownTypes() {
        val counts = linkedMapOf("FutureCoffee" to 9, Latte.dbKey to 4, Cappuccino.dbKey to 1)
        assertEquals(DayCoffee(mapOf(Cappuccino to 1, Latte to 4)), counts.toDayCoffee())
    }

    @Test
    fun dataMapRoundTripKeepsDateAndCounts() {
        val original = DatedDayCoffee(today, day)
        val decoded = DataMap().also { original.toDataMap(it) }.toDatedDayCoffee(fallbackDate)
        assertEquals(today, decoded?.date)
        assertEquals(day.toCountsByDbKey(), decoded?.dayCoffee?.toCountsByDbKey())
    }

    @Test
    fun countsByDbKeyWinOverLegacyPositionalCounts() {
        val map = DataMap().apply {
            putDataMap(COUNTS_BY_DB_KEY, DataMap().apply { putInt(Latte.dbKey, 7) })
            putIntegerArrayList(LEGACY_POSITIONAL_COUNTS_KEY, DayCoffee(mapOf(Cappuccino to 5)).toCountsList())
        }
        assertEquals(DayCoffee(mapOf(Latte to 7)), map.toDatedDayCoffee(fallbackDate)?.dayCoffee)
    }

    @Test
    fun legacyPayloadWithoutDateIsStillReadable() {
        val map = DataMap().apply { putIntegerArrayList(LEGACY_POSITIONAL_COUNTS_KEY, day.toCountsList()) }
        val decoded = map.toDatedDayCoffee(fallbackDate)
        assertEquals(fallbackDate, decoded?.date)
        assertEquals(day.toCountsByDbKey(), decoded?.dayCoffee?.toCountsByDbKey())
    }

    @Test
    fun payloadWithoutCountsIsIgnored() {
        assertNull(DataMap().apply { putString(DATE_KEY, today.toString()) }.toDatedDayCoffee(fallbackDate))
    }
}
