package ru.beryukhov.coffeegram.components

import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.DetailsConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinkTest {

    @Test
    fun testPlainPath() {
        assertEquals(listOf("map"), "https://coffeegram.pages.dev/map".pathSegments())
    }

    @Test
    fun testNestedPath() {
        assertEquals(
            listOf("calendar", "day", "2026-01-15"),
            "https://coffeegram.pages.dev/calendar/day/2026-01-15".pathSegments(),
        )
    }

    @Test
    fun testQueryAndFragmentAreStripped() {
        assertEquals(listOf("stats"), "https://coffeegram.pages.dev/stats?admin=1".pathSegments())
        assertEquals(listOf("settings"), "https://coffeegram.pages.dev/settings#theme".pathSegments())
    }

    @Test
    fun testPortIsNotAPathSegment() {
        assertEquals(listOf("map"), "http://localhost:8080/map".pathSegments())
    }

    @Test
    fun testRootHasNoSegments() {
        assertEquals(emptyList(), "https://coffeegram.pages.dev/".pathSegments())
        assertEquals(emptyList(), "https://coffeegram.pages.dev".pathSegments())
        assertEquals(emptyList(), "https://coffeegram.pages.dev/?admin".pathSegments())
    }

    @Test
    fun testDayListConfig() {
        assertEquals(
            DetailsConfig.DayList(LocalDate(2026, 1, 15)),
            listOf("day", "2026-01-15").toDayListConfig(),
        )
    }

    @Test
    fun testNoDayListConfig() {
        assertNull(emptyList<String>().toDayListConfig())
        assertNull(listOf("day").toDayListConfig())
        assertNull(listOf("day", "not-a-date").toDayListConfig())
        assertNull(listOf("day", "2026-01-15", "extra").toDayListConfig())
        assertNull(listOf("week", "2026-01-15").toDayListConfig())
    }
}
