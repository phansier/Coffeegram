package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koin.android.ext.android.inject
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.model.getThemeStoreStub
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.pages.SettingsAppBar
import ru.beryukhov.coffeegram.pages.SettingsPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage
import ru.beryukhov.coffeegram.repository.ThemeSharedPrefStorage

class MainActivity : AppCompatActivity() {

    private val themeStore: ThemeStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val transition = newSplashTransition()
            Box {
                LandingPage(
                    modifier = Modifier.alpha(transition.splashAlpha),
                )
                PagesContent(
                    modifier = Modifier.alpha(transition.contentAlpha),
                    topPadding = transition.contentTopPadding,
                    navigationStore = NavigationStore(),
                    daysCoffeesStore = DaysCoffeesStore(),
                    themeStore = themeStore
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(
        navigationStore = NavigationStore(),
        daysCoffeesStore = DaysCoffeesStore(),
        themeStore = getThemeStoreStub(LocalContext.current)
    )
}

@Composable
fun PagesContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    navigationStore: NavigationStore,
    daysCoffeesStore: DaysCoffeesStore,
    themeStore: ThemeStore
) {
    val navigationState: NavigationState by navigationStore.state.collectAsState()
    val themeState: ThemeState by themeStore.state.collectAsState()
    CoffeegramTheme(
        darkTheme =
        isDarkTheme(themeState)
    ) {
        Scaffold(
            modifier,
            topBar = {
                when (navigationState) {
                    is NavigationState.TablePage -> TableAppBar(
                        (navigationState as NavigationState.TablePage).yearMonth,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListAppBar(navigationStore)
                    is NavigationState.SettingsPage -> SettingsAppBar()
                }
            },
        ) {
            Column {
                Spacer(
                    Modifier
                        .padding(top = topPadding)
                        .align(Alignment.CenterHorizontally)
                )
                val currentNavigationState = navigationState
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TablePage(
                        currentNavigationState.yearMonth,
                        daysCoffeesStore,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        daysCoffeesStore,
                        currentNavigationState.date
                    )
                    is NavigationState.SettingsPage -> {
                        SettingsPage(themeStore)
                    }
                }
                BottomNavigation(modifier = modifier) {
                    BottomNavigationItem(selected = currentNavigationState is NavigationState.TablePage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ReturnToTablePage
                            )
                        },
                        label = { Text(stringResource(id = R.string.calendar)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "",
                            )
                        }
                    )
                    BottomNavigationItem(selected = currentNavigationState is NavigationState.SettingsPage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ToSettingsPage
                            )
                        },
                        label = { Text(stringResource(id = R.string.settings)) },
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

@Composable
private fun isDarkTheme(themeState: ThemeState) = when (themeState) {
    ThemeState.DARK -> true
    ThemeState.LIGHT -> false
    ThemeState.SYSTEM -> isSystemInDarkTheme()
}
