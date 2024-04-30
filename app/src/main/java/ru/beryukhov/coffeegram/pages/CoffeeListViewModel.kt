package ru.beryukhov.coffeegram.pages

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.nowLD

interface CoffeeListViewModel {
    @Composable
    fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<Pair<CoffeeType, Int>>

    fun newIntent(intent: DaysCoffeesIntent)
    fun newIntent(intent: NavigationIntent)
}

object CoffeeListViewModelStub : CoffeeListViewModel {
    override fun newIntent(intent: DaysCoffeesIntent) = Unit
    override fun newIntent(intent: NavigationIntent) = Unit

    @Composable
    override fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<Pair<CoffeeType, Int>> =
        emptyMap<CoffeeType, Int>().withEmpty().toPersistentList()
}

val localDateStub: LocalDate = nowLD()

class CoffeeListViewModelImpl(
    private val daysCoffeesStore: DaysCoffeesStore,
    private val navigationStore: NavigationStore
) : ViewModel(), CoffeeListViewModel {
    @Composable
    override fun getDayCoffeesWithEmpty(localDate: LocalDate): PersistentList<Pair<CoffeeType, Int>> {
        val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
        val dayCoffee = dayCoffeeState.value[localDate] ?: DayCoffee()
        return dayCoffee.coffeeCountMap.withEmpty().toPersistentList()
    }

    override fun newIntent(intent: DaysCoffeesIntent) {
        daysCoffeesStore.newIntent(intent)
    }

    override fun newIntent(intent: NavigationIntent) {
        navigationStore.newIntent(intent)
    }
}

@VisibleForTesting
internal fun Map<CoffeeType, Int>.withEmpty(): List<Pair<CoffeeType, Int>> {
    data class MutablePair(val ct: CoffeeType, var count: Int)

    val emptyList: MutableList<MutablePair> =
        CoffeeType.values().toList().map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { it.ct to it.count }
}
