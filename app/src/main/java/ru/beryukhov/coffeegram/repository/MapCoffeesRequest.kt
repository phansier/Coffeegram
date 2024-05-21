package ru.beryukhov.coffeegram.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

private val client = HttpClient()

suspend fun coffeeShops(): List<CoffeeShop> {
    try {
        val response =
            client.get("https://raw.githubusercontent.com/specialtycoffeecyprus/api/develop/database/seeders/CafeSeeder.php")
                .bodyAsText()
        return parseResponse(response)

    } catch (_: Exception) {
        return emptyList()
    }
}

fun parseResponse(response: String): List<CoffeeShop> {
    val regex = Regex("""(?!\[)((\n.*'.*){4,}(\n.*))(?=\])""")
    return regex.findAll(response).mapNotNull {
        val block = it.groupValues[0].lines().map { it.trim() }
        val name = param(block, "name")
        val description = param(block, "description")
        val latitude = param(block, "latitude")?.toDoubleOrNull()
        val longitude = param(block, "longitude")?.toDoubleOrNull()

        if (name != null && description != null && latitude != null && longitude != null) {
            CoffeeShop(name, description, latitude, longitude)
        } else null
    }.toList()
}

private fun param(block: List<String>, paramName: String) =
    block.firstOrNull { it.startsWith("'$paramName' => ") }
        ?.replace("'$paramName' => ", "")
        ?.removePrefix("'")
        ?.removePrefix("\"")
        ?.removeSuffix(",")
        ?.removeSuffix("'")
        ?.removeSuffix("\"")

data class CoffeeShop (
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
)

