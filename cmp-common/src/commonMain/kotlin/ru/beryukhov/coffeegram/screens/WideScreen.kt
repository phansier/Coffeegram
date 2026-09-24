package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Width breakpoint above which the UI switches to its wide-screen layout
 * (navigation rail instead of bottom bar, side-by-side / two-pane content).
 */
internal val WIDE_SCREEN_THRESHOLD = 600.dp
private val EXPANDED_WIDTH_THRESHOLD = 840.dp
private val COMPACT_HEIGHT_THRESHOLD = 480.dp

@Immutable
internal data class WindowLayout(
    val isWide: Boolean = false,
    val isExpandedWidth: Boolean = false,
    val isCompactHeight: Boolean = false,
)

internal val LocalWindowLayout = compositionLocalOf { WindowLayout() }

@Composable
internal fun WideLayoutProvider(
    modifier: Modifier = Modifier,
    content: @Composable (showNavigationRail: Boolean) -> Unit,
) {
    val isTabletop = currentWindowAdaptiveInfo().windowPosture.isTabletop
    BoxWithConstraints(modifier = modifier) {
        val windowLayout = WindowLayout(
            isWide = maxWidth >= WIDE_SCREEN_THRESHOLD,
            isExpandedWidth = maxWidth >= EXPANDED_WIDTH_THRESHOLD,
            isCompactHeight = maxHeight < COMPACT_HEIGHT_THRESHOLD,
        )
        CompositionLocalProvider(LocalWindowLayout provides windowLayout) {
            content(windowLayout.isWide && !isTabletop)
        }
    }
}
