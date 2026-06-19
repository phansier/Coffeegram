package ru.beryukhov.coffeegram.admin

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.beryukhov.coffeegram.repository.SupabaseConfig

@Serializable
data class AdminSession(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String = "",
)

internal class SupabaseAuth(
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    private val client = HttpClient {
        install(ContentNegotiation) { json(json) }
    }

    private val redirectTo: String get() = "${locationOrigin()}/?admin"

    suspend fun sendMagicLink(email: String): Result<Unit> = runCatching {
        val response = client.post("${SupabaseConfig.URL}/auth/v1/otp") {
            header("apikey", SupabaseConfig.PUBLISHABLE_KEY)
            parameter("redirect_to", redirectTo)
            contentType(ContentType.Application.Json)
            setBody(OtpRequest(email = email))
        }
        check(response.status.isSuccess()) { "Magic link request failed: ${response.status}" }
    }

    fun captureSessionFromUrl(): AdminSession? {
        val params = locationHash().removePrefix("#").split("&").mapNotNull { pair ->
            val parts = pair.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
        val accessToken = params["access_token"] ?: return null
        val session = AdminSession(
            accessToken = accessToken,
            refreshToken = params["refresh_token"].orEmpty(),
        )
        save(session)
        replaceUrl(redirectTo)
        return session
    }

    fun loadSession(): AdminSession? {
        val raw = storageGet(STORAGE_KEY)
        if (raw.isEmpty()) return null
        return runCatching { json.decodeFromString<AdminSession>(raw) }.getOrNull()
    }

    fun signOut() = storageRemove(STORAGE_KEY)

    private fun save(session: AdminSession) = storageSet(STORAGE_KEY, json.encodeToString(session))

    @Serializable
    private data class OtpRequest(
        val email: String,
        @SerialName("create_user") val createUser: Boolean = false,
    )

    private companion object {
        const val STORAGE_KEY = "coffeegram_admin_session"
    }
}
