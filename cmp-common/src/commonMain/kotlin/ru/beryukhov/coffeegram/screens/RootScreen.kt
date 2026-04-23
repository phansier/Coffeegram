@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.components.RootComponent
import ru.beryukhov.coffeegram.model.NavBarItem
import ru.beryukhov.coffeegram.model.calendar
import ru.beryukhov.coffeegram.model.getNavBarItems
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
        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets.systemBars,
            topBar = { TopBar(rootComponent) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = { BottomBar(rootComponent, navBarItems) }
        ) { paddingValues ->
            CurrentScreen(rootComponent, paddingValues, snackbarHostState)
        }
    }
}

@Composable
private fun TopBar(rootComponent: RootComponent) {
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.fillMaxWidth(),
    ) { _, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CmpCoffeeEditAppBar(c.component)
            is RootComponent.Child.Stats -> StatsAppBar()
            is RootComponent.Child.Map -> MapAppBar()
            is RootComponent.Child.Settings -> SettingsAppBar(c.component)
        }
    }
}

@Composable
private fun CurrentScreen(
    rootComponent: RootComponent,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
) {
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.padding(paddingValues),
    ) { _, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CmpCoffeeEditScreen(c.component)
            is RootComponent.Child.Stats -> StatsScreen(c.component)
            is RootComponent.Child.Map -> MapScreen(c.component)
            is RootComponent.Child.Settings -> SettingsScreen(
                component = c.component,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
internal fun BottomBar(
    rootComponent: RootComponent,
    navBarItems: PersistentList<NavBarItem>,
) {
    NavigationBar {
        val currentIndex by rootComponent.pages.subscribeAsState()
        navBarItems.forEachIndexed { index, item ->
            NavigationBarItem(
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
