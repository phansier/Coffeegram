package ru.beryukhov.coffeegram.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.nowLD

interface CoffeeListViewModel {
    @Composable
    fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<CoffeeTypeWithCount>

    fun decrementCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    )

    fun incrementCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    )

    fun newIntent(intent: NavigationIntent)
}

object CoffeeListViewModelStub : CoffeeListViewModel {
    override fun newIntent(intent: NavigationIntent) = Unit

    override fun decrementCoffee(localDate: LocalDate, coffeeType: CoffeeType) = Unit
    override fun incrementCoffee(localDate: LocalDate, coffeeType: CoffeeType) = Unit

    @Composable
    override fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<CoffeeTypeWithCount> =
        emptyMap<CoffeeType, Int>().withEmpty().toPersistentList()
}

val localDateStub: LocalDate = nowLD()

class CoffeeListViewModelImpl(
    private val daysCoffeesStore: DaysCoffeesStore,
    private val navigationStore: NavigationStore
) : ViewModel(), CoffeeListViewModel {
    @Composable
    override fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<CoffeeTypeWithCount> {
        val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
        val dayCoffee = dayCoffeeState.coffees[localDate] ?: DayCoffee()
        return dayCoffee.coffeeCountMap.withEmpty().toPersistentList()
    }

    override fun decrementCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    ) {
        newIntent(
            DaysCoffeesIntent.MinusCoffee(
                localDate = localDate,
                coffeeType = coffeeType
            )
        )
    }

    override fun incrementCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    ) {
        newIntent(
            DaysCoffeesIntent.PlusCoffee(
                localDate = localDate,
                coffeeType = coffeeType
            )
        )
    }

    private fun newIntent(intent: DaysCoffeesIntent) {
        daysCoffeesStore.newIntent(intent)
    }

    override fun newIntent(intent: NavigationIntent) {
        navigationStore.newIntent(intent)
    }
}
