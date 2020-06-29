package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.material.Scaffold
import androidx.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.TablePage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PagesContent(NavigationStore(), DaysCoffeesStore())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(NavigationStore(), DaysCoffeesStore())
}

@Composable
fun PagesContent(navigationStore: NavigationStore, daysCoffeesStore: DaysCoffeesStore) {
    val navigationState by navigationStore.state.collectAsState()
    CoffeegramTheme {
        Scaffold() {
            Column {
                when (navigationState) {
                    is NavigationState.TablePage -> TablePage(navigationState.yearMonth, daysCoffeesStore, navigationStore)
                    is NavigationState.CoffeeListPage -> CoffeeListPage(daysCoffeesStore, navigationStore)
                }
            }
        }
    }
}
