package ru.beryukhov.coffeegram

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage

//@OptIn(ExperimentalCoroutinesApi::class)
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


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(navigationStore = NavigationStore(), daysCoffeesStore = DaysCoffeesStore())
}