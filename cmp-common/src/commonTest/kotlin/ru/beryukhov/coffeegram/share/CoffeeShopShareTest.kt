package ru.beryukhov.coffeegram.share

import ru.beryukhov.coffeegram.repository.CoffeeShop
import kotlin.test.Test
import kotlin.test.assertEquals

class CoffeeShopShareTest {

    private val shop = CoffeeShop(name = "Blue Bottle", description = "", latitude = 0.0, longitude = 0.0, id = "42")

    @Test
    fun testShareLinkPointsToShopOnMap() {
        assertEquals("https://coffeegram.pages.dev/map/42", shop.shareLink())
    }

    @Test
    fun testShareLinkFallsBackToMapWithoutId() {
        assertEquals("https://coffeegram.pages.dev/map", shop.copy(id = "").shareLink())
    }

    @Test
    fun testShareText() {
        assertEquals("Blue Bottle - Coffeegram\nhttps://coffeegram.pages.dev/map/42", shop.shareText())
    }
}
