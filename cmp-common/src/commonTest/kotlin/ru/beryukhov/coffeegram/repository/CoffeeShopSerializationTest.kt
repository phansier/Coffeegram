package ru.beryukhov.coffeegram.repository

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

class CoffeeShopSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesPostgrestRowsIgnoringUnknownColumns() {
        val response = """
            [
              {
                "id": "9f3a1c",
                "name": "Coltivare",
                "description": "Cozy roastery counter",
                "latitude": 35.1739,
                "longitude": 33.3618,
                "tags": ["v60", "batch"],
                "created_at": "2026-06-01T00:00:00Z",
                "updated_at": "2026-06-18T14:22:33Z"
              },
              {
                "id": "2b77e0",
                "name": "Reload",
                "description": "",
                "latitude": 35.1756,
                "longitude": 33.3641,
                "tags": [],
                "updated_at": null
              }
            ]
        """.trimIndent()

        val shops = json.decodeFromString<List<CoffeeShop>>(response)

        assertEquals(2, shops.size)
        val first = shops[0]
        assertEquals("9f3a1c", first.id)
        assertEquals("Coltivare", first.name)
        assertEquals(listOf("v60", "batch"), first.tags)
        assertEquals(Instant.parse("2026-06-18T14:22:33Z"), first.updatedAt)
        assertNull(shops[1].updatedAt)
    }

    @Test
    fun parsesTimestampWithOffsetAndMicroseconds() {
        val shop = decodeShop("""{"name":"x","description":"","latitude":0.0,"longitude":0.0,"updated_at":"2026-06-18T14:22:33.123456+00:00"}""")
        assertNotNull(shop.updatedAt)
    }

    @Test
    fun malformedTimestampDegradesToNull() {
        val shop = decodeShop("""{"name":"x","description":"","latitude":0.0,"longitude":0.0,"updated_at":"not-a-timestamp"}""")
        assertNull(shop.updatedAt)
    }

    @Test
    fun appliesDefaultsForMissingFields() {
        val shop = decodeShop("""{"name":"x","description":"","latitude":0.0,"longitude":0.0}""")
        assertEquals("", shop.id)
        assertEquals(emptyList(), shop.tags)
        assertNull(shop.updatedAt)
    }

    private fun decodeShop(raw: String): CoffeeShop = json.decodeFromString<CoffeeShop>(raw)
}
