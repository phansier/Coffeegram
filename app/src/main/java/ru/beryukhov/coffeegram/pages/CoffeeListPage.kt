package ru.beryukhov.coffeegram.pages

import androidx.compose.Composable
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
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.times

@Composable
fun CoffeeListPage() {
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack) } }
    )
    CoffeeList(
        coffeeTypes = listOf(
            CoffeeType(
                R.drawable.header,
                "Cappucino"
            ),
            CoffeeType(
                R.drawable.header,
                "Latte"
            )
        ) * 6,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun CoffeeList(coffeeTypes: List<CoffeeType>, modifier: Modifier = Modifier) {
    AdapterList(data = coffeeTypes, modifier = modifier.fillMaxHeight()) { type ->
        CoffeeTypeItem(type)
    }
}