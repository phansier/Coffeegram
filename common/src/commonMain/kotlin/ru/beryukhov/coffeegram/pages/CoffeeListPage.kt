package ru.beryukhov.coffeegram.pages

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.date_time.local_date.LocalDate
import ru.beryukhov.coffeegram.date_time.local_date.now
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.CoffeeTypeItem


@Composable
fun CoffeeListAppBar(navigationStore: NavigationStore){
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = {
            IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ""
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
    val dayCoffee = dayCoffeeState.coffees[localDate]?: DayCoffee()
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        itemsIndexed(items = dayCoffee.coffeeCountMap.toList(),
            itemContent = { _, pair ->
                CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
            })
    }
}

//@Preview
@Composable
fun Preview() {
    CoffeeList(
        now(),
        DaysCoffeesStore(),
        modifier = Modifier
    )
}