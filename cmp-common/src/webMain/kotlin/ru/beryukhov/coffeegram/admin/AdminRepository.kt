package ru.beryukhov.coffeegram.admin

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.beryukhov.coffeegram.repository.CoffeeShop
import ru.beryukhov.coffeegram.repository.SupabaseConfig

@Serializable
data class CoffeeShopInput(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String>,
)

internal class AdminAuthException : Exception("Admin session expired — please sign in again")

internal class AdminRepository(
    private val accessToken: () -> String,
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    private val client = HttpClient {
        install(ContentNegotiation) { json(json) }
    }

    private val endpoint = "${SupabaseConfig.URL}/rest/v1/coffee_shops"

    suspend fun list(): Result<List<CoffeeShop>> = runCatching {
        val response = client.get(endpoint) {
            supabaseHeaders()
            parameter("select", "*")
            parameter("order", "name")
        }
        response.ensureOk()
        response.body<List<CoffeeShop>>()
    }

    suspend fun create(input: CoffeeShopInput): Result<CoffeeShop> = runCatching {
        val response = client.post(endpoint) {
            supabaseHeaders()
            returnRepresentation()
            contentType(ContentType.Application.Json)
            setBody(listOf(input))
        }
        response.ensureOk()
        response.body<List<CoffeeShop>>().first()
    }

    suspend fun update(id: String, input: CoffeeShopInput): Result<CoffeeShop> = runCatching {
        val response = client.patch(endpoint) {
            supabaseHeaders()
            parameter("id", "eq.$id")
            returnRepresentation()
            contentType(ContentType.Application.Json)
            setBody(input)
        }
        response.ensureOk()
        response.body<List<CoffeeShop>>().first()
    }

    suspend fun delete(id: String): Result<Unit> = runCatching {
        val response = client.delete(endpoint) {
            supabaseHeaders()
            parameter("id", "eq.$id")
        }
        response.ensureOk()
    }

    private fun HttpRequestBuilder.supabaseHeaders() {
        header("apikey", SupabaseConfig.PUBLISHABLE_KEY)
        header(HttpHeaders.Authorization, "Bearer ${accessToken()}")
    }

    private fun HttpRequestBuilder.returnRepresentation() {
        header("Prefer", "return=representation")
    }

    private fun HttpResponse.ensureOk() {
        if (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden) {
            throw AdminAuthException()
        }
        check(status.isSuccess()) { "Supabase request failed: $status" }
    }
}
