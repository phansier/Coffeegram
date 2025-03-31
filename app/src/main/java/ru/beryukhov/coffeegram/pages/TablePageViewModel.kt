package ru.beryukhov.coffeegram.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.coffeegram.data.getDayIconCoffeeType
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.YearMonth

interface TablePageViewModel {
    @Composable
    fun getFilledDayItemsMap(yearMonth: YearMonth): Map<Int, Picture>

    fun newIntent(intent: NavigationIntent)
}

object TablePageViewModelStub : TablePageViewModel {
    @Composable
    override fun getFilledDayItemsMap(yearMonth: YearMonth): Map<Int, Picture> = emptyMap()
    override fun newIntent(intent: NavigationIntent) = Unit
}

class TablePageViewModelImpl(
    private val daysCoffeesStore: DaysCoffeesStore,
    private val navigationStore: NavigationStore
) : ViewModel(), TablePageViewModel {
    @Composable
    override fun getFilledDayItemsMap(yearMonth: YearMonth): Map<Int, Picture> {
        val coffeesState by daysCoffeesStore.state.collectAsState()
        return coffeesState.coffees
            .filter { entry: Map.Entry<LocalDate, DayCoffee> ->
                entry.key.year == yearMonth.year
                    && entry.key.month == yearMonth.month
            }
            .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
            .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getDayIconCoffeeType() }
    }

    override fun newIntent(intent: NavigationIntent) {
        navigationStore.newIntent(intent)
    }
}
