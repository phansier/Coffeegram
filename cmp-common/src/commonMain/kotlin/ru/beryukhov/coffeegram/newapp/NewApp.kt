package ru.beryukhov.coffeegram.newapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.calendar
import coffeegram.cmp_common.generated.resources.settings
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.slapps.cupertino.adaptive.AdaptiveNavigationBar
import com.slapps.cupertino.adaptive.AdaptiveNavigationBarItem
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
@Suppress("ModifierMissing")
fun NewApp(rootComponent: RootComponent, modifier: Modifier = Modifier) {

    CoffeegramTheme(
        themeState = rootComponent.themeState.collectAsState().value,
    ) {
        AdaptiveScaffold(
            modifier = modifier,
            topBar = {
                ChildPages(
                    pages = rootComponent.pages,
                    onPageSelected = rootComponent::selectPage,
                    modifier = Modifier.fillMaxWidth(),
                ) { index, page ->
                    when (val c = page) {
                        is RootComponent.Child.Table -> TableAppBar(c.component)
                        is RootComponent.Child.Settings -> SettingsAppBar(c.component)
                    }
                }
            },
            bottomBar = {
                AdaptiveNavigationBar {
                    val currentIndex by rootComponent.pages.subscribeAsState()
                    AdaptiveNavigationBarItem(
                        selected = currentIndex.selectedIndex == 0,
                        onClick = {
                            rootComponent.selectPage(0)
                        },
                        label = { Text(stringResource(Res.string.calendar)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "",
                            )
                        }
                    )
                    AdaptiveNavigationBarItem(
                        selected = currentIndex.selectedIndex == 1,
                        onClick = {
                            rootComponent.selectPage(1)
                        },
                        label = { Text(stringResource(Res.string.settings)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "",
                            )
                        }
                    )
                }
            }
        ) { paddingValues ->
            ChildPages(
                pages = rootComponent.pages,
                onPageSelected = rootComponent::selectPage,
                modifier = Modifier.padding(paddingValues),
            ) { index, page ->
                when (val c = page) {
                    is RootComponent.Child.Table -> TableScreen(c.component)
                    is RootComponent.Child.Settings -> SettingsScreen(c.component)
                }
            }
        }
    }
}
