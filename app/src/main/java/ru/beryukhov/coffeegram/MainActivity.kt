package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.animations.*
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.pages.TablePage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var splashShown by remember { mutableStateOf(SplashState.Shown) }
            val transition = transition(splashTransitionDefinition, splashShown)
            Stack {
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
        Scaffold(modifier) {
            Column {
                Spacer(Modifier.padding(top = topPadding))
                when (navigationState) {
                    is NavigationState.TablePage -> TablePage(navigationState.yearMonth, daysCoffeesStore, navigationStore)
                    is NavigationState.CoffeeListPage -> CoffeeListPage(daysCoffeesStore, navigationStore)
                }
            }
        }
    }
}
