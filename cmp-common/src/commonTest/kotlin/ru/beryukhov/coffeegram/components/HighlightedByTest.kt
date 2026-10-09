package ru.beryukhov.coffeegram.components

import ru.beryukhov.coffeegram.repository.CoffeeShop
import kotlin.test.Test
import kotlin.test.assertEquals

class HighlightedByTest {

    private val first = CoffeeShop(name = "First", description = "", latitude = 1.0, longitude = 1.0, id = "a")
    private val second = CoffeeShop(name = "Second", description = "", latitude = 2.0, longitude = 2.0, id = "b")
    private val duplicateOfSecond = second.copy()

    @Test
    fun testMatchingShopIsHighlighted() {
        assertEquals(
            listOf(false, true),
            listOf(first, second).highlightedBy { it.id == "b" }.map { it.highlighted },
        )
    }

    @Test
    fun testNothingHighlightedWithoutMatch() {
        assertEquals(
            listOf(false, false),
            listOf(first, second).highlightedBy { it.id == "missing" }.map { it.highlighted },
        )
    }

    @Test
    fun testOnlyFirstMatchIsHighlighted() {
        assertEquals(
            listOf(false, true, false),
            listOf(first, second, duplicateOfSecond).highlightedBy { it == second }.map { it.highlighted },
        )
    }
}
