package ru.beryukhov.coffeegram.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore

interface TablePageViewModel {
    @Composable
    fun getFilledDayItemsMap(yearMonth: YearMonth):Map<Int, Int?>

    fun newIntent(intent: NavigationIntent)
}

object TablePageViewModelStub: TablePageViewModel {
    @Composable
    override fun getFilledDayItemsMap(yearMonth: YearMonth):Map<Int, Int?> = emptyMap()
    override fun newIntent(intent: NavigationIntent) = Unit
}

class TablePageViewModelImpl(
    private val daysCoffeesStore: DaysCoffeesStore,
    private val navigationStore: NavigationStore
): ViewModel(), TablePageViewModel {
    @Composable
    override fun getFilledDayItemsMap(yearMonth: YearMonth):Map<Int, Int?> {
        val coffeesState by daysCoffeesStore.state.collectAsState()
        return coffeesState.value
            .filter { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.year == yearMonth.year && entry.key.month == yearMonth.month }
            .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
            .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getIconId() }
    }

    override fun newIntent(intent: NavigationIntent) {
        navigationStore.newIntent(intent)
    }
}
