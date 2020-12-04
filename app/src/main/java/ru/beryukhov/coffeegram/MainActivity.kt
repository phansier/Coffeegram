package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.animations.*
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var splashShown by remember { mutableStateOf(SplashState.Shown) }
            val transition = transition(splashTransitionDefinition, splashShown)
            Box {
                LandingPage(
                    modifier = Modifier.drawOpacity(transition[splashAlphaKey]),
                    onTimeout = { splashShown = SplashState.Completed }
                )
                PagesContent(
                    modifier = Modifier.drawOpacity(transition[contentAlphaKey]),
                    topPadding = transition[contentTopPaddingKey],
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
                Spacer(Modifier.padding(top = topPadding).align(Alignment.CenterHorizontally))
                when (navigationState) {
                    is NavigationState.TablePage -> TablePage(
                        navigationState.yearMonth,
                        daysCoffeesStore,
                        navigationStore
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        daysCoffeesStore,
                        navigationStore
                    )
                }
            }
        }
    }
}
