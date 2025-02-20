package ru.beryukhov.coffeegram

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.calendar
import coffeegram.cmp_common.generated.resources.settings
import com.slapps.cupertino.adaptive.AdaptiveNavigationBar
import com.slapps.cupertino.adaptive.AdaptiveNavigationBarItem
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.SettingsAppBar
import ru.beryukhov.coffeegram.pages.SettingsPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage

@Composable
fun PagesContent(
    navigationStore: NavigationStore,
    daysCoffeesStore: DaysCoffeesStore,
    themeStore: ThemeStore,
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    ) {

    val navigationState: NavigationState by navigationStore.state.collectAsState()
    val currentNavigationState = navigationState
    val snackbarHostState = remember { SnackbarHostState() }
    CoffeegramTheme(
        themeState = themeState(themeStore)
    ) {
        AdaptiveScaffold(
            modifier = modifier,
            topBar = {
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TableAppBar(
                        yearMonth = currentNavigationState.yearMonth,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListAppBar(
                        navigationStore
                    )
                    is NavigationState.SettingsPage -> SettingsAppBar()
                }
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                Spacer(Modifier.padding(top = topPadding).align(Alignment.CenterHorizontally))
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TablePage(
                        yearMonth = currentNavigationState.yearMonth,
                        daysCoffeesStore = daysCoffeesStore,
                        navigationStore = navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        daysCoffeesStore = daysCoffeesStore,
                        navigationState = currentNavigationState
                    )
                    is NavigationState.SettingsPage -> SettingsPage(
                        themeStore = themeStore,
                        snackbarHostState = snackbarHostState,
                    )
                }
                AdaptiveNavigationBar {
                    AdaptiveNavigationBarItem(
                        selected = currentNavigationState is NavigationState.TablePage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ReturnToTablePage
                            )
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
                        selected = currentNavigationState is NavigationState.SettingsPage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ToSettingsPage
                            )
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
        }
    }
}

data class Dependencies(
    val navigationStore: NavigationStore,
    val daysCoffeesStore: DaysCoffeesStore,
    val themeStore: ThemeStore,
)

@Composable
fun DefaultPreview(dependencies: Dependencies) {
    PagesContent(
        navigationStore = dependencies.navigationStore,
        daysCoffeesStore = dependencies.daysCoffeesStore,
        themeStore = dependencies.themeStore
    )
}

@Composable
private fun themeState(themeStore: ThemeStore): ThemeState {
    val themeState: ThemeState by themeStore.state.collectAsState()
    return themeState
}
