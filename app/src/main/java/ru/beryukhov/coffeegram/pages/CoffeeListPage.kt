package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.*
import ru.beryukhov.coffeegram.view.CoffeeTypeItem


@Composable
fun CoffeeListAppBar(navigationStore: NavigationStore){
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = {
            IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    Icons.Default.ArrowBack
                )
            }
        }
    )
}

@Composable
fun CoffeeListPage(daysCoffeesStore: DaysCoffeesStore, navigationStore: NavigationStore) {
    CoffeeList(
        (navigationStore.state.value as NavigationState.CoffeeListPage).date,
        daysCoffeesStore
    )
}

//@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CoffeeList(
    localDate: LocalDate,
    daysCoffeesStore: DaysCoffeesStore,
    modifier: Modifier = Modifier
) {
    val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
    val dayCoffee = dayCoffeeState.coffees[localDate]?:DayCoffee()
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        items(items = dayCoffee.coffeeCountMap.toList(),
            itemContent = { pair ->
                CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
            })
    }
}

//@Preview
@Composable
private fun Preview() {
    CoffeeList(
        LocalDate.now(),
        DaysCoffeesStore(),
        modifier = Modifier
    )
}