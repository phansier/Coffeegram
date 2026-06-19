package ru.beryukhov.coffeegram.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val LOG_HTTP = false

private val json = Json {
    ignoreUnknownKeys = true
}

private val client = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
    if (LOG_HTTP) {
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP", message)
                }
            }
        }
    }
}

expect object Log {
    fun d(tag: String, message: String)
}

suspend fun coffeeShops(): List<CoffeeShop> = try {
    client.get("${SupabaseConfig.URL}/rest/v1/coffee_shops") {
        header("apikey", SupabaseConfig.PUBLISHABLE_KEY)
        header(HttpHeaders.Authorization, "Bearer ${SupabaseConfig.PUBLISHABLE_KEY}")
        parameter("select", "*")
        parameter("order", "name")
    }.body<List<CoffeeShop>>()
} catch (_: Exception) {
    emptyList()
}
