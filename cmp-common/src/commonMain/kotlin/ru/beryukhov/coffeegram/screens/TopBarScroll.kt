@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.adaptive.MaterialTopAppBarAdaptation
import com.slapps.cupertino.adaptive.Theme
import com.slapps.cupertino.adaptive.currentTheme

internal val LocalTopBarScrollBehavior = staticCompositionLocalOf<TopAppBarScrollBehavior?> { null }

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
internal fun rememberTopBarScrollBehavior(): TopAppBarScrollBehavior? {
    val behavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    return behavior.takeIf { currentTheme != Theme.Cupertino }
}

@Composable
internal fun Modifier.hideTopBarOnScroll(scrollableState: ScrollableState): Modifier {
    val behavior = LocalTopBarScrollBehavior.current
    DisposableEffect(behavior) {
        onDispose { behavior?.state?.reset() }
    }
    val connection = remember(behavior, scrollableState) {
        behavior?.let { ScrollableContentConnection(it, scrollableState) }
    }
    return if (connection == null) this else nestedScroll(connection)
}

@Composable
internal fun Modifier.verticalScrollHidingTopBar(): Modifier {
    val scrollState = rememberScrollState()
    return hideTopBarOnScroll(scrollState).verticalScroll(scrollState)
}

internal fun MaterialTopAppBarAdaptation.applyTopBarScrollBehavior(behavior: TopAppBarScrollBehavior?) {
    scrollBehavior = behavior
    colors = colors.copy(scrolledContainerColor = colors.containerColor)
}

private fun TopAppBarState.reset() {
    heightOffset = 0f
    contentOffset = 0f
}

private class ScrollableContentConnection(
    private val behavior: TopAppBarScrollBehavior,
    private val scrollableState: ScrollableState,
) : NestedScrollConnection {
    private val isActive: Boolean
        get() = scrollableState.canScrollForward ||
            scrollableState.canScrollBackward ||
            behavior.state.heightOffset < 0f

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        if (isActive) behavior.nestedScrollConnection.onPreScroll(available, source) else Offset.Zero

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset =
        if (isActive) behavior.nestedScrollConnection.onPostScroll(consumed, available, source) else Offset.Zero

    override suspend fun onPreFling(available: Velocity): Velocity =
        behavior.nestedScrollConnection.onPreFling(available)

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        behavior.nestedScrollConnection.onPostFling(consumed, available)
}
