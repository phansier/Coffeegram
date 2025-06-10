package ru.beryukhov.coffeegram.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.date_time_utils.getFullMonthName

@ExperimentalMaterial3Api
@Composable
fun CoffeeListAppBar(
    localDate: LocalDate,
    modifier: Modifier = Modifier,
    coffeeListViewModel: CoffeeListViewModel = koinViewModel<CoffeeListViewModelImpl>()
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                "${localDate.dayOfMonth} ${getFullMonthName(localDate.month).take(3)} "
                    + stringResource(R.string.add_drink)
            )
        },
        navigationIcon = {
            IconButton(onClick = { coffeeListViewModel.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
fun CoffeeListPage(
    localDate: LocalDate,
    modifier: Modifier = Modifier,
    coffeeListViewModel: CoffeeListViewModel = koinViewModel<CoffeeListViewModelImpl>()
) {
    BackHandler { coffeeListViewModel.newIntent(NavigationIntent.ReturnToTablePage) }
    val onPlusClick = remember(localDate, coffeeListViewModel) {
        { coffeeType: CoffeeType ->
            coffeeListViewModel.incrementCoffee(localDate, coffeeType)
        }
    }
    val onMinusClick = remember(localDate, coffeeListViewModel) {
        { coffeeType: CoffeeType ->
            coffeeListViewModel.decrementCoffee(localDate, coffeeType)
        }
    }
    CoffeeList(
        coffeeItems = coffeeListViewModel.getDayCoffeesWithEmpty(localDate).toPersistentList(),
        onPlusClick = onPlusClick,
        onMinusClick = onMinusClick,
        modifier = modifier
    )
}

@Composable
private fun CoffeeList(
    coffeeItems: PersistentList<CoffeeTypeWithCount>,
    onPlusClick: (coffeeType: CoffeeType) -> Unit,
    modifier: Modifier = Modifier,
    onMinusClick: (coffeeType: CoffeeType) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .lazyListLength(coffeeItems.size)
            .testTag("CoffeeList")
    ) {
        itemsIndexed(items = coffeeItems) { index, model ->
            val (type, count) = model
            CoffeeTypeItem(
                coffeeType = type,
                count = count,
                onIncrement = { onPlusClick(type) },
                onDecrement = { onMinusClick(type) },
                modifier = Modifier.lazyListItemPosition(index)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CoffeeList(
        coffeeItems = CoffeeListViewModelStub.getDayCoffeesWithEmpty(localDate = localDateStub),
        onPlusClick = { },
        onMinusClick = { }
    )
}
