@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.util.reflect.instanceOf
import org.koin.compose.koinInject
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.model.getNavBarItemsOld
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.MapAppBar
import ru.beryukhov.coffeegram.pages.MapPage
import ru.beryukhov.coffeegram.pages.SettingsAppBar
import ru.beryukhov.coffeegram.pages.SettingsPage
import ru.beryukhov.coffeegram.pages.StatsAppBar
import ru.beryukhov.coffeegram.pages.StatsPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage
import ru.beryukhov.date_time_utils.nowYM
import ru.beryukhov.date_time_utils.toTotalMonths

@Preview(showBackground = true)
@Composable
internal fun DefaultPreview() {
    PagesContent()
}

@Composable
fun PagesContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    navigationStore: NavigationStore = koinInject(),
    startWearableActivity: () -> Unit = {},
    showMap: Boolean = false
) {
    val navigationState: NavigationState by navigationStore.state.collectAsState()
    val currentNavigationState = navigationState
    val snackbarHostState = remember { SnackbarHostState() }

    // You can scroll all the way up to the year 3000 with page count set to 36 000 --> (3000 * 12)
    val pagerState = rememberPagerState(
        pageCount = { 36_000 },
        initialPage = if (currentNavigationState is NavigationState.TablePage) {
            currentNavigationState.yearMonth.toTotalMonths()
        } else {
            nowYM().toTotalMonths()
        }
    )

    val navBarItems = remember(showMap) { getNavBarItemsOld(showMap) }

    CoffeegramTheme(
        themeState = themeState()
    ) {
        Scaffold(modifier, topBar = {
            when (currentNavigationState) {
                is NavigationState.TablePage -> TableAppBar(
                    pagerState = pagerState
                )

                is NavigationState.CoffeeListPage -> CoffeeListAppBar(
                    localDate = currentNavigationState.date
                )
                is NavigationState.StatsPage -> StatsAppBar()
                is NavigationState.SettingsPage -> SettingsAppBar()

                is NavigationState.MapPage -> MapAppBar()
            }
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .testTag(currentNavigationState.mapTestTag())
            ) {
                Spacer(
                    Modifier
                        .padding(top = topPadding)
                        .align(Alignment.CenterHorizontally)
                )
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TablePage(
                        pagerState = pagerState
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        localDate = currentNavigationState.date
                    )
                    is NavigationState.StatsPage -> StatsPage()
                    is NavigationState.SettingsPage -> SettingsPage(
                        themeStore = koinInject(),
                        snackbarHostState = snackbarHostState,
                        startWearableActivity = startWearableActivity,
                    )
                    is NavigationState.MapPage -> MapPage()
                }
                NavigationBar {
                    navBarItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentNavigationState.instanceOf(item.page),
                            onClick = { navigationStore.newIntent(item.intent) },
                            label = { Text(stringResource(id = item.title)) },
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
        }
    }
}

@Composable
private fun themeState(): ThemeState {
    val themeState: ThemeState by koinInject<ThemeStore>().state.collectAsState()
    return themeState
}
