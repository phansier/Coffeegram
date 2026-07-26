@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAdaptiveApi::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.slapps.cupertino.adaptive.AdaptiveNavigationBar
import com.slapps.cupertino.adaptive.AdaptiveNavigationBarItem
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.components.RootComponent
import ru.beryukhov.coffeegram.model.NavBarItem
import ru.beryukhov.coffeegram.model.calendar
import ru.beryukhov.coffeegram.model.settings
import ru.beryukhov.coffeegram.model.specialty
import ru.beryukhov.coffeegram.model.stats
import ru.beryukhov.coffeegram.screens.CoffeeEditAppBar as CmpCoffeeEditAppBar
import ru.beryukhov.coffeegram.screens.CoffeeEditScreen as CmpCoffeeEditScreen

@Composable
fun RootScreen(
    rootComponent: RootComponent,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBarItems = remember(rootComponent.showMap) { getNavBarItems(rootComponent.showMap) }

    CoffeegramTheme(
        themeState = rootComponent.themeState.collectAsState().value,
    ) {
        BoxWithConstraints(modifier = modifier) {
            val isWide = maxWidth >= WIDE_SCREEN_THRESHOLD
            if (isWide) {
                Row(modifier = Modifier.fillMaxSize()) {
                    NavRail(rootComponent, navBarItems)
                    Scaffold(
                        contentWindowInsets = WindowInsets.systemBars,
                        topBar = { TopBar(rootComponent, swipeEnabled = false) },
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    ) { paddingValues ->
                        CurrentScreen(rootComponent, paddingValues, snackbarHostState, swipeEnabled = false)
                    }
                }
            } else {
                Scaffold(
                    contentWindowInsets = WindowInsets.systemBars,
                    topBar = { TopBar(rootComponent, swipeEnabled = true) },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = { BottomBar(rootComponent, navBarItems) }
                ) { paddingValues ->
                    CurrentScreen(rootComponent, paddingValues, snackbarHostState, swipeEnabled = true)
                }
            }
        }
    }
}

@Composable
private fun TopBar(rootComponent: RootComponent, swipeEnabled: Boolean) {
    val pagesState by rootComponent.pages.subscribeAsState()
    val pagerSwipeEnabled = swipeEnabled && !pagesState.isMapSelected()
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.fillMaxWidth(),
        pager = { modifier, state, key, pageContent ->
            HorizontalPager(
                modifier = modifier,
                state = state,
                key = key,
                userScrollEnabled = pagerSwipeEnabled,
                pageContent = pageContent,
            )
        },
    ) { _, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CmpCoffeeEditAppBar(c.component)
            is RootComponent.Child.Stats -> StatsAppBar()
            is RootComponent.Child.Map -> MapAppBar(c.component)
            is RootComponent.Child.Settings -> SettingsAppBar(c.component)
        }
    }
}

@Composable
private fun CurrentScreen(
    rootComponent: RootComponent,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    swipeEnabled: Boolean,
) {
    val pagesState by rootComponent.pages.subscribeAsState()
    val pagerSwipeEnabled = swipeEnabled && !pagesState.isMapSelected()
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.fillMaxSize(),
        pager = { modifier, state, key, pageContent ->
            HorizontalPager(
                modifier = modifier,
                state = state,
                key = key,
                userScrollEnabled = pagerSwipeEnabled,
                pageContent = pageContent,
            )
        },
    ) { _, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CmpCoffeeEditScreen(c.component, paddingValues)
            is RootComponent.Child.Stats -> StatsScreen(
                component = c.component,
                modifier = Modifier.padding(paddingValues),
            )
            is RootComponent.Child.Map -> SpecialtyScreen(
                component = c.component,
                modifier = Modifier.padding(paddingValues),
            )
            is RootComponent.Child.Settings -> SettingsScreen(
                component = c.component,
                snackbarHostState = snackbarHostState,
                contentPadding = paddingValues,
            )
        }
    }
}

private fun ChildPages<*, RootComponent.Child>.isMapSelected(): Boolean =
    items.getOrNull(selectedIndex)?.instance is RootComponent.Child.Map

@Composable
internal fun BottomBar(
    rootComponent: RootComponent,
    navBarItems: PersistentList<NavBarItem>,
) {
    AdaptiveNavigationBar {
        val currentIndex by rootComponent.pages.subscribeAsState()
        navBarItems.forEachIndexed { index, item ->
            AdaptiveNavigationBarItem(
                selected = currentIndex.selectedIndex == index,
                onClick = { rootComponent.selectPage(index) },
                label = {
                    Text(
                        text = stringResource(item.title),
                        style = typography.bodySmall
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "",
                    )
                }
            )
        }
    }
}

@Composable
internal fun NavRail(
    rootComponent: RootComponent,
    navBarItems: PersistentList<NavBarItem>,
) {
    NavigationRail {
        val currentIndex by rootComponent.pages.subscribeAsState()
        navBarItems.forEachIndexed { index, item ->
            NavigationRailItem(
                selected = currentIndex.selectedIndex == index,
                onClick = { rootComponent.selectPage(index) },
                label = {
                    Text(
                        text = stringResource(item.title),
                        style = typography.bodySmall
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "",
                    )
                }
            )
        }
    }
}

internal fun getNavBarItems(showMap: Boolean): PersistentList<NavBarItem> =
    if (showMap) {
        persistentListOf(calendar, stats, specialty, settings)
    } else {
        persistentListOf(calendar, stats, settings)
    }
