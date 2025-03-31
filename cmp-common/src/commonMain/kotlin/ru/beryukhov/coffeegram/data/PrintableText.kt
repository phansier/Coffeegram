package ru.beryukhov.coffeegram.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Immutable
sealed interface PrintableText {
    suspend fun get(): String

    companion object {
        val EMPTY: PrintableText = Raw("")
    }

    @Immutable
    data class Raw(val text: String) : PrintableText {
        override suspend fun get(): String = text
    }

    @Immutable
    data class Res(val resource: StringResource) : PrintableText {
        override suspend fun get(): String = getString(resource)
    }
}

@Stable
fun PrintableText(string: String): PrintableText =
    PrintableText.Raw(string)

@Stable
fun String.asPrintableText(): PrintableText =
    PrintableText.Raw(this)

@Stable
@Composable
fun printableText(text: PrintableText): String =
    when (text) {
        is PrintableText.Raw -> text.text
        is PrintableText.Res -> stringResource(text.resource)
    }

@Stable
@Composable
fun PrintableText.resolve(): String = printableText(this)
