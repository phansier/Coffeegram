package ru.beryukhov.coffeegram.share

import androidx.compose.runtime.Composable

@Composable
internal expect fun rememberNativeShare(): ((text: String) -> Unit)?
