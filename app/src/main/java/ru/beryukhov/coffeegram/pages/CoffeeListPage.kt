package ru.beryukhov.coffeegram.pages

import androidx.compose.Composable
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
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.DayCoffeeFlow
import ru.beryukhov.coffeegram.data.IntFlow
import ru.beryukhov.coffeegram.view.CoffeeTypeItem

@Composable
fun CoffeeListPage(dayCoffeeFlow: DayCoffeeFlow, dateFlow: MutableStateFlow<Int>) {
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = { IconButton(onClick = {dateFlow.value = -1}) { Icon(Icons.Default.ArrowBack) } }
    )
    CoffeeList(
        dayCoffeeFlow,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun CoffeeList(dayCoffeeFlow: DayCoffeeFlow, modifier: Modifier = Modifier) {
    val dayCoffee by dayCoffeeFlow.getState()
    AdapterList(
        data = dayCoffee.coffeeCountMap.toList(),
        modifier = modifier.fillMaxHeight()
    ) { pair ->
        CoffeeTypeItem(pair.first, IntFlow(dayCoffeeFlow, pair.first))
    }
}

@Preview
@Composable
private fun preview() {
    CoffeeList(
        DayCoffeeFlow(MutableStateFlow(mapOf(LocalDate.now() to DayCoffee())), LocalDate.now()),
        modifier = Modifier.weight(1f)
    )
}