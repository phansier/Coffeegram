package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Width breakpoint above which the UI switches to its wide-screen layout
 * (navigation rail instead of bottom bar, side-by-side / two-pane content).
 */
internal val WIDE_SCREEN_THRESHOLD = 600.dp

internal val LocalIsWideLayout = compositionLocalOf { false }

@Composable
internal fun WideLayoutProvider(
    modifier: Modifier = Modifier,
    content: @Composable (isWide: Boolean) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val isWide = maxWidth >= WIDE_SCREEN_THRESHOLD
        CompositionLocalProvider(LocalIsWideLayout provides isWide) {
            content(isWide)
        }
    }
}
