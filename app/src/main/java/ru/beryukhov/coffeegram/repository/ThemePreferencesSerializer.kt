package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ThemePreferencesSerializer : Serializer<ThemePreferences> {
    override val defaultValue: ThemePreferences = ThemePreferences.getDefaultInstance().toBuilder()
        .setThemeState(ThemePreferences.ProtoThemeState.SYSTEM).build()

    override suspend fun readFrom(input: InputStream): ThemePreferences {
        try {
            return ThemePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ThemePreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
