package ru.beryukhov.coffeegram.repository

// import io.ktor.client.plugins.logging.LogLevel
// import io.ktor.client.plugins.logging.Logger
// import io.ktor.client.plugins.logging.Logging
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

private val client = HttpClient {
//    if (BuildConfig.DEBUG) {
//        install(Logging) {
//            level = LogLevel.INFO
//            logger = object : Logger {
//                override fun log(message: String) {
//                    Log.d("HTTP", message)
//                }
//            }
//        }
//    }
}

suspend fun coffeeShops(): List<CoffeeShop> {
    try {
        val response =
            client.get(
                "https://raw.githubusercontent.com/specialtycoffeecyprus/api/" +
                    "develop/database/seeders/CafeSeeder.php"
            ).bodyAsText()
        return parseResponse(response)
    } catch (_: Exception) {
        return emptyList()
    }
}

fun parseResponse(response: String): List<CoffeeShop> {
    // Split the PHP seeder into per-cafe chunks delimited by "],". The previous regex-based
    // implementation caused catastrophic backtracking on Kotlin/Native (iOS).
    return response.split("],").mapNotNull { chunk ->
        val block = chunk.lines().map { it.trim() }
        val name = param(block, "name")
        val description = param(block, "description")
        val latitude = param(block, "latitude")?.toDoubleOrNull()
        val longitude = param(block, "longitude")?.toDoubleOrNull()
        if (name != null && description != null && latitude != null && longitude != null) {
            CoffeeShop(name, description, latitude, longitude)
        } else {
            null
        }
    }
}

private fun param(block: List<String>, paramName: String) =
    block.firstOrNull { it.startsWith("'$paramName' => ") }
        ?.replace("'$paramName' => ", "")
        ?.removePrefix("'")
        ?.removePrefix("\"")
        ?.removeSuffix(",")
        ?.removeSuffix("'")
        ?.removeSuffix("\"")
