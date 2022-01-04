package ru.beryukhov.coffeegram.pages

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.CoffeeTypeItem


@Composable
fun CoffeeListAppBar(navigationStore: NavigationStore){
    TopAppBar(title = { Text(stringResource(R.string.add_drink)) },
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
fun CoffeeListPage(localDate: LocalDate) {
    CoffeeList(
        localDate = localDate,
        coffeeListViewModel = getViewModel<CoffeeListViewModelImpl>()
    )
}


@Composable
fun CoffeeList(
    localDate: LocalDate,
    modifier: Modifier = Modifier,
    coffeeListViewModel: CoffeeListViewModel,
) {
    val coffeeItems = coffeeListViewModel.getDayCoffeesWithEmpty(localDate)
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        itemsIndexed(items = coffeeItems,
            itemContent = { _, pair: Pair<CoffeeType, Int> ->
                CoffeeTypeItem(localDate, pair.first, pair.second, coffeeListViewModel)
            })
    }
}

@Preview
@Composable
private fun Preview() {
    CoffeeList(localDateStub, coffeeListViewModel = CoffeeListViewModelStub)
}