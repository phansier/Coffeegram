package ru.beryukhov.coffeegram.pages

// import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.coffeeTypeValues
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.CoffeeTypeItem

@Composable
fun CoffeeListAppBar(navigationStore: NavigationStore) {
    TopAppBar(
        title = { Text("Add drink") },
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
fun CoffeeListPage(daysCoffeesStore: DaysCoffeesStore, navigationState: NavigationState.CoffeeListPage) {
    CoffeeList(
        navigationState.date,
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
    val dayCoffee = dayCoffeeState.coffees[localDate] ?: DayCoffee()
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        itemsIndexed(
            items = dayCoffee.coffeeCountMap.withEmpty(),
            itemContent = { _, pair: Pair<CoffeeType, Int> ->
                CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
            }
        )
    }
}

class MutablePair(val ct: CoffeeType, var count: Int)

// @VisibleForTesting
internal fun Map<CoffeeType, Int>.withEmpty(): List<Pair<CoffeeType, Int>> {
    val emptyList: MutableList<MutablePair> =
        coffeeTypeValues().toList().map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { it.ct to it.count }
}
