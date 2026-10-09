package ru.beryukhov.coffeegram.share

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

internal actual fun plainTextClipEntry(text: String): ClipEntry =
    ClipData.newPlainText("Coffeegram", text).toClipEntry()
