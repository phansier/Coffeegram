package ru.beryukhov.coffeegram.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.view.CoffeeTypeItem

@ExperimentalMaterial3Api
@Composable
fun CoffeeListAppBar(
    localDate: LocalDate,
    modifier: Modifier = Modifier,
    coffeeListViewModel: CoffeeListViewModel = getViewModel<CoffeeListViewModelImpl>()
) {
    SmallTopAppBar(
        modifier = modifier,
        title = { Text(localDate.format(dateFormatter) + " " + stringResource(R.string.add_drink)) },
        navigationIcon = {
            IconButton(onClick = { coffeeListViewModel.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ""
                )
            }
        }
    )
}

private val dateFormatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
    .appendLiteral(' ')
    .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
    .toFormatter()

@Composable
fun CoffeeListPage(
    localDate: LocalDate,
    coffeeListViewModel: CoffeeListViewModel = getViewModel<CoffeeListViewModelImpl>()
) {
    BackHandler { coffeeListViewModel.newIntent(NavigationIntent.ReturnToTablePage) }
    CoffeeList(
        coffeeItems = coffeeListViewModel.getDayCoffeesWithEmpty(localDate),
        onPlusClick = { coffeeType: CoffeeType ->
            coffeeListViewModel.newIntent(
                DaysCoffeesIntent.PlusCoffee(
                    localDate,
                    coffeeType
                )
            )
        },
        onMinusClick = { coffeeType: CoffeeType ->
            coffeeListViewModel.newIntent(
                DaysCoffeesIntent.MinusCoffee(
                    localDate,
                    coffeeType
                )
            )
        }
    )
}

@Composable
private fun CoffeeList(
    coffeeItems: List<Pair<CoffeeType, Int>>,
    onPlusClick: (coffeeType: CoffeeType) -> Unit,
    modifier: Modifier = Modifier,
    onMinusClick: (coffeeType: CoffeeType) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        itemsIndexed(items = coffeeItems,
            itemContent = { _, pair: Pair<CoffeeType, Int> ->
                CoffeeTypeItem(
                    coffeeType = pair.first,
                    count = pair.second,
                    onPlusClick = { onPlusClick(pair.first) },
                    onMinusClick = { onMinusClick(pair.first) }
                )
            })
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
