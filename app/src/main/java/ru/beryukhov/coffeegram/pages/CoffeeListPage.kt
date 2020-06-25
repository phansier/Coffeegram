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
import kotlinx.coroutines.flow.MutableStateFlow
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.Cappucino
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.Latte
import ru.beryukhov.coffeegram.times

@Composable
fun CoffeeListPage(dayCoffeeFlow: MutableStateFlow<DayCoffee>) {
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack) } }
    )
    CoffeeList(
        dayCoffeeFlow,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun CoffeeList(dayCoffeeFlow: MutableStateFlow<DayCoffee>, modifier: Modifier = Modifier) {
    val dayCoffee by dayCoffeeFlow.collectAsState()
    AdapterList(
        data = dayCoffee.coffeeCountMap.toList(),
        modifier = modifier.fillMaxHeight()
    ) { pair ->
        CoffeeTypeItem(pair.first, pair.second)
    }
}

@Preview
@Composable
private fun preview() {
    CoffeeList(
        MutableStateFlow(DayCoffee(mapOf(Cappucino to 5))),
        modifier = Modifier.weight(1f)
    )
}