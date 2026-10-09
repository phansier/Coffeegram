package ru.beryukhov.coffeegram.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

internal const val JS_CAN_SHARE = "(typeof navigator.share === 'function')"

internal const val JS_SHARE_TEXT = "navigator.share({ text: text }).catch(function() {})"

internal expect fun jsCanShare(): Boolean

internal expect fun jsShareText(text: String)

@Composable
internal actual fun rememberNativeShare(): ((text: String) -> Unit)? =
    remember { if (jsCanShare()) ::jsShareText else null }
