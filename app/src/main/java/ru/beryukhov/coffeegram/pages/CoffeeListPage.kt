package ru.beryukhov.coffeegram.pages

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.ColumnScope.weight
import androidx.ui.layout.fillMaxHeight
import androidx.ui.material.IconButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.tooling.preview.Preview
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.*
import ru.beryukhov.coffeegram.view.CoffeeTypeItem

@Composable
fun CoffeeListPage(daysCoffeesStore: DaysCoffeesStore, navigationStore: NavigationStore) {
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = {
            IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    Icons.Default.ArrowBack
                )
            }
        }
    )
    CoffeeList(
        (navigationStore.state.value as NavigationState.CoffeeListPage).date,
        daysCoffeesStore,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun CoffeeList(
    localDate: LocalDate,
    daysCoffeesStore: DaysCoffeesStore,
    modifier: Modifier = Modifier
) {
    val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
    val dayCoffee = dayCoffeeState.coffees[localDate]?:DayCoffee()
    AdapterList(
        data = dayCoffee.coffeeCountMap.toList(),
        modifier = modifier.fillMaxHeight()
    ) { pair ->
        CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
    }
}

@Preview
@Composable
private fun preview() {
    CoffeeList(
        LocalDate.now(),
        DaysCoffeesStore(),
        modifier = Modifier.weight(1f)
    )
}