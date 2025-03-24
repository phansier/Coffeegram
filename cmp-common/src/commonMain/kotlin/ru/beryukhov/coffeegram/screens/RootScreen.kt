package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.slapps.cupertino.adaptive.AdaptiveNavigationBar
import com.slapps.cupertino.adaptive.AdaptiveNavigationBarItem
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.components.RootComponent
import ru.beryukhov.coffeegram.model.NavBarItem
import ru.beryukhov.coffeegram.model.getNavBarItems

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun RootScreen(rootComponent: RootComponent, modifier: Modifier = Modifier) {
    val navBarItems = remember { getNavBarItems() }
    CoffeegramTheme(
        themeState = rootComponent.themeState.collectAsState().value,
    ) {
        AdaptiveScaffold(
            modifier = modifier,
            topBar = { TopBar(rootComponent) },
            bottomBar = { BottomBar(rootComponent, navBarItems) }
        ) { paddingValues ->
            CurrentScreen(rootComponent, paddingValues)
        }
    }
}

@Composable
private fun TopBar(rootComponent: RootComponent) {
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.fillMaxWidth(),
    ) { index, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CoffeeEditAppBar(c.component)
            is RootComponent.Child.Settings -> SettingsAppBar(c.component)
        }
    }
}

@Composable
private fun CurrentScreen(
    rootComponent: RootComponent,
    paddingValues: PaddingValues
) {
    ChildPages(
        pages = rootComponent.pages,
        onPageSelected = rootComponent::selectPage,
        modifier = Modifier.padding(paddingValues),
    ) { index, page ->
        when (val c = page) {
            is RootComponent.Child.CoffeeEdit -> CoffeeEditScreen(c.component)
            is RootComponent.Child.Settings -> SettingsScreen(c.component)
        }
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
private fun BottomBar(
    rootComponent: RootComponent,
    navBarItems: PersistentList<NavBarItem>
) {
    AdaptiveNavigationBar {
        val currentIndex by rootComponent.pages.subscribeAsState()
        navBarItems.forEachIndexed { index, item ->
            AdaptiveNavigationBarItem(
                selected = currentIndex.selectedIndex == index,
                onClick = {
                    rootComponent.selectPage(index)
                },
                label = { Text(stringResource(item.title)) },
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
