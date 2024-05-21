package ru.beryukhov.coffeegram.repository

import kotlin.test.Test
import kotlin.test.assertEquals

class CoffeeShopsParserTest {
    @Test
    fun testRegex() {
        val response = """[
            [
                'name' => 'Roasters',
                'description' => 'Cosy coffee shop',
                'place_id' => 'ChI',
                'latitude' => '34.9143997',
                'longitude' => '33.634976',
            ],
            [
                'name' => "Roasters",
                'description' => 'Cosy coffee shop',
                'place_id' => 'ChI',
                'latitude' => 34.9143997,
                'longitude' => 33.634976,
            ],"""
        val roasters = CoffeeShop(
            name = "Roasters",
            description = "Cosy coffee shop",
            latitude = 34.9143997,
            longitude = 33.634976
        )
        assertEquals(listOf(roasters, roasters), parseResponse(response))
    }
}
