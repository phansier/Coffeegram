package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage

class MainActivity : AppCompatActivity() {
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
                    NavigationStore(), DaysCoffeesStore()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(navigationStore = NavigationStore(), daysCoffeesStore = DaysCoffeesStore())
}

@Composable
fun PagesContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    navigationStore: NavigationStore,
    daysCoffeesStore: DaysCoffeesStore
) {
    val navigationState: NavigationState by navigationStore.state.collectAsState()
    CoffeegramTheme {
        Scaffold(
            modifier, topBar = {
                when (navigationState) {
                    is NavigationState.TablePage -> TableAppBar(
                        navigationState.yearMonth,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListAppBar(navigationStore)
                }
            }
        ) {
            Column {
                Spacer(
                    Modifier
                        .padding(top = topPadding)
                        .align(Alignment.CenterHorizontally))
                val currentNavigationState = navigationState
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TablePage(
                        currentNavigationState.yearMonth,
                        daysCoffeesStore,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        daysCoffeesStore,
                        currentNavigationState
                    )
                }
            }
        }
    }
}
