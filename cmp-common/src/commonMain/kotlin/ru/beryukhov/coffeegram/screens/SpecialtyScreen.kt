package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.link_copied
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.view.CoffeeShopList
import ru.beryukhov.coffeegram.view.CoffeeShopListStyle

/**
 * Specialty tab: on expanded or short wide windows the map and the coffee-shop list sit side by
 * side; otherwise the list lives in a draggable bottom sheet over the map instead.
 */
@Composable
fun SpecialtyScreen(
    component: MapComponent,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onLinkCopied: () -> Unit = {
        scope.launch { snackbarHostState.showSnackbar(getString(Res.string.link_copied)) }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (LocalWindowLayout.current.showsMapSidePane()) {
            val sidePaneWidth = (maxWidth * SIDE_PANE_FRACTION).coerceIn(MinSidePaneWidth, MaxSidePaneWidth)
            Row(modifier = Modifier.fillMaxSize()) {
                MapScreen(
                    component = component,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    showMarkerDescription = false,
                )
                VerticalDivider()
                SidePane(
                    modifier = Modifier.fillMaxHeight(),
                    width = sidePaneWidth,
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val listState = rememberLazyListState()
                        CoffeeShopList(
                            component = component,
                            listState = listState,
                            modifier = Modifier.fillMaxSize().hideTopBarOnScroll(listState),
                            style = CoffeeShopListStyle.Card,
                            onLinkCopied = onLinkCopied,
                        )
                        ListSnackbarHost(snackbarHostState)
                    }
                }
            }
        } else {
            BottomSheetPane(
                modifier = Modifier.fillMaxSize(),
                sheetContent = {
                    val listState = rememberLazyListState()
                    CoffeeShopList(
                        component = component,
                        listState = listState,
                        modifier = Modifier.fillMaxWidth().hideTopBarOnScroll(listState),
                        style = CoffeeShopListStyle.Flat,
                        onLinkCopied = onLinkCopied,
                    )
                },
            ) {
                MapScreen(
                    component = component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            ListSnackbarHost(snackbarHostState)
        }
    }
}

@Composable
private fun BoxScope.ListSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState, modifier = Modifier.align(Alignment.BottomCenter))
}

private fun WindowLayout.showsMapSidePane(): Boolean =
    isExpandedWidth || isWide && isCompactHeight

private const val SIDE_PANE_FRACTION = 0.4f
private val MinSidePaneWidth = 280.dp
private val MaxSidePaneWidth = 400.dp
