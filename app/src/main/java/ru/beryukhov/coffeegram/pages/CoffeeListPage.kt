package ru.beryukhov.coffeegram.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.map
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

@Composable
fun CoffeeList(
    localDate: LocalDate,
    daysCoffeesStore: DaysCoffeesStore,
    modifier: Modifier = Modifier
) {
    val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
    Log.d("TEST_", "stated $dayCoffeeState")
    val dayCoffee = dayCoffeeState.value[localDate]?:DayCoffee()
    Column(modifier = modifier.fillMaxHeight()) {
        for (pair in dayCoffee.coffeeCountMap.toList()) {
            CoffeeTypeItem(
                localDate = localDate,
                coffeeType = pair.first,
                count = pair.second,
                daysCoffeesStore = daysCoffeesStore
            )
        }
        /*items(
            items = dayCoffee.coffeeCountMap.toList(),
            itemContent = { pair ->
                CoffeeTypeItem(
                    localDate = localDate,
                    coffeeType = pair.first,
                    count = pair.second,
                    daysCoffeesStore = daysCoffeesStore
                )
            })*/
    }
}

@Preview
@Composable
private fun Preview() {
    CoffeeList(
        LocalDate.now(),
        DaysCoffeesStore(),
        modifier = Modifier
    )
}