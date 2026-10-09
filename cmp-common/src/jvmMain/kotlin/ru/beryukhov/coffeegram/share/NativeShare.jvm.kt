package ru.beryukhov.coffeegram.share

import androidx.compose.runtime.Composable

@Composable
internal actual fun rememberNativeShare(): ((text: String) -> Unit)? = null
