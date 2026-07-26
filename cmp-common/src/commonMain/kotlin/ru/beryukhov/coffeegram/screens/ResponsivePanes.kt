package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
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
 * Narrow-screen bottom sheet: [content] (e.g. the map) fills the whole pane, with a draggable
 * sheet floating over its bottom edge showing [sheetContent] (e.g. the coffee-shop list).
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

    Box(modifier = modifier) {
        content()

        UnstyledBottomSheet(state = sheetState, modifier = Modifier.fillMaxWidth()) {
            Sheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = SheetCornerRadius, topEnd = SheetCornerRadius))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Column {
                    DragIndication(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 10.dp)
                            .size(width = DragHandleWidth, height = DragHandleHeight)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                    )
                    sheetContent()
                }
            }
        }
    }
}
