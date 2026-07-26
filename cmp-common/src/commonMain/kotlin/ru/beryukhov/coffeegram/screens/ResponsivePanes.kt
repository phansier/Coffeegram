package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.rememberBottomSheetState

/**
 * Wide-screen side panel: a fixed-width column sitting next to the main content (e.g. the map),
 * separated from it by the caller's own `VerticalDivider()` — matching the same idiom
 * `SettingsScreen` already uses for its category list.
 */
@Composable
fun SidePane(
    modifier: Modifier = Modifier,
    width: Dp = 320.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface),
        content = content,
    )
}

private val SheetCornerRadius = 28.dp
private val DragHandleWidth = 36.dp
private val DragHandleHeight = 4.dp

/**
 * Whether the platform's map implementation can render *underneath* other Compose content that
 * overlaps it. True everywhere except web: there, the map is a real HTML/WebGL element pinned
 * above the Compose canvas at the maximum z-index (MapLibre needs a real DOM/WebGL surface, not
 * something Compose's canvas can host directly) — so anything Compose draws where the map still
 * extends, including the sheet's own drag handle and rounded corners, is invisible there.
 */
internal expect val mapRendersUnderOverlappingContent: Boolean

/**
 * Narrow-screen bottom sheet: [content] (e.g. the map) sits above a draggable sheet showing
 * [sheetContent] (e.g. the coffee-shop list). Where [mapRendersUnderOverlappingContent] is true,
 * [content] is resized in real time to end [SheetCornerRadius] below where the sheet begins, so
 * the sheet's rounded top corners always have a sliver of it visible behind them (matching native
 * bottom sheet look) while the flat majority of the sheet sits on uncovered ground. Where it's
 * false (web), [content] ends exactly where the sheet begins instead — any overlap there would
 * just hide the sheet's handle/corners under the map, since nothing Compose draws can render
 * above it.
 * Built on Compose Unstyled's [UnstyledBottomSheet], which is intentionally unstyled — this is
 * where the app's own corner radius, drag handle, and background get applied.
 */
@Composable
fun BottomSheetPane(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    peekHeight: Dp = 140.dp,
    expandedFraction: Float = 0.56f,
    content: @Composable BoxScope.() -> Unit,
) {
    val peek = remember(peekHeight) {
        SheetDetent("peek") { containerHeight, _ -> peekHeight.coerceAtMost(containerHeight) }
    }
    val expanded = remember(expandedFraction) {
        SheetDetent("expanded") { containerHeight, _ -> containerHeight * expandedFraction }
    }
    val sheetState = rememberBottomSheetState(
        initialDetent = peek,
        detents = listOf(peek, expanded),
    )

    BoxWithConstraints(modifier = modifier) {
        val sheetHeight = with(LocalDensity.current) { sheetState.offset.toDp() }
        val overlap = if (mapRendersUnderOverlappingContent) SheetCornerRadius else 0.dp
        val contentHeight = (maxHeight - sheetHeight + overlap).coerceIn(0.dp, maxHeight)

        Box(modifier = Modifier.fillMaxWidth().height(contentHeight)) {
            content()
        }

        UnstyledBottomSheet(state = sheetState, modifier = Modifier.matchParentSize()) {
            Sheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(topStart = SheetCornerRadius, topEnd = SheetCornerRadius),
                    ),
            ) {
                Column {
                    DragIndication(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 10.dp)
                            .size(width = DragHandleWidth, height = DragHandleHeight)
                            .background(MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    )
                    sheetContent()
                }
            }
        }
    }
}
