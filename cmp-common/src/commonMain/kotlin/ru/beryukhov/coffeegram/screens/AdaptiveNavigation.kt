package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slapps.cupertino.adaptive.AdaptiveNavigationBar
import com.slapps.cupertino.adaptive.AdaptiveNavigationBarItem
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.model.NavBarItem

@Composable
internal fun AdaptiveNavigationContainer(
    showNavigationRail: Boolean,
    items: PersistentList<NavBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (bottomBar: @Composable () -> Unit) -> Unit,
) {
    Row(modifier = modifier) {
        if (showNavigationRail) {
            AppNavigationRail(items, selectedIndex, onSelect)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .consumeRailInsets(showNavigationRail),
        ) {
            content(
                if (showNavigationRail) {
                    {}
                } else {
                    { AppNavigationBar(items, selectedIndex, onSelect) }
                }
            )
        }
    }
}

@Composable
private fun Modifier.consumeRailInsets(showNavigationRail: Boolean): Modifier {
    val railStartInsets = WindowInsets.systemBars.only(WindowInsetsSides.Start)
    return if (showNavigationRail) consumeWindowInsets(railStartInsets) else this
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
internal fun AppNavigationBar(
    items: PersistentList<NavBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            AdaptiveNavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { NavigationLabel(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
            )
        }
    }
}

@Composable
private fun AppNavigationRail(
    items: PersistentList<NavBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        items.forEachIndexed { index, item ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = { NavigationLabel(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
            )
        }
    }
}

@Composable
private fun NavigationLabel(item: NavBarItem) {
    Text(
        text = stringResource(item.title),
        style = typography.bodySmall,
    )
}
