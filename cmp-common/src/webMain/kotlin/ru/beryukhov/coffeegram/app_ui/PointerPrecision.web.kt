package ru.beryukhov.coffeegram.app_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

internal const val JS_FINE_POINTER_QUERY = "window.matchMedia('(pointer: fine)').matches"

internal expect fun matchesFinePointerMedia(): Boolean

@Composable
internal actual fun hasFinePointer(): Boolean = remember { matchesFinePointerMedia() }
