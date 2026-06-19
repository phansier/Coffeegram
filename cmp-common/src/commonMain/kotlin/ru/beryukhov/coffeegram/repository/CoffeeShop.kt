package ru.beryukhov.coffeegram.repository

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

/**
 * A curated coffee shop, sourced from the Supabase `coffee_shops` table.
 *
 * [id] and [updatedAt] are admin-only concerns and are not shown in the user UI. [tags] defaults are
 * suggested in the admin UI but the column accepts any custom value.
 */
@Serializable
data class CoffeeShop(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val id: String = "",
    val tags: List<String> = emptyList(),
    @SerialName("updated_at")
    @Serializable(with = TolerantInstantSerializer::class)
    val updatedAt: Instant? = null,
) {
    override fun toString(): String {
        return "CoffeeShop(name='$name', lat=$latitude, lng=$longitude)"
    }
}

/**
 * Parses a Postgres `timestamptz` into an [Instant], degrading a malformed/unexpected value to null
 */
internal object TolerantInstantSerializer : KSerializer<Instant?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UpdatedAt", PrimitiveKind.STRING).nullable

    override fun serialize(encoder: Encoder, value: Instant?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant? {
        if (!decoder.decodeNotNullMark()) {
            decoder.decodeNull()
            return null
        }
        return runCatching { Instant.parse(decoder.decodeString()) }.getOrNull()
    }
}
