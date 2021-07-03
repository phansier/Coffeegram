package ru.beryukhov.coffeegram.model

import androidx.compose.runtime.State
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.getByName
import ru.beryukhov.repository.CoffeeRepository
import ru.beryukhov.repository.model.DateCoffees
import ru.beryukhov.repository.model.DbDayCoffee

class DaysCoffeesStore : Store<DaysCoffeesIntent, DaysCoffeesState>(
    initialState = DaysCoffeesState(mapOf())
) {

    val repository: CoffeeRepository by lazy { CoffeeRepository() }

    override suspend fun handleIntent(intent: DaysCoffeesIntent): DaysCoffeesState {
        return when (intent) {
            is DaysCoffeesIntent.PlusCoffee -> increaseCoffee(intent.localDate, intent.coffeeType)
            is DaysCoffeesIntent.MinusCoffee -> decreaseCoffee(intent.localDate, intent.coffeeType)
        }
    }

    private suspend fun increaseCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    ): DaysCoffeesState {
        return putCoffeeCount(
            localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.plus(1) ?: 1
        )
    }

    private suspend fun decreaseCoffee(
        localDate: LocalDate,
        coffeeType: CoffeeType
    ): DaysCoffeesState {
        return putCoffeeCount(
            localDate, coffeeType,
            getCoffeeOrNull(localDate, coffeeType)?.minus(1) ?: 0
        )
    }

    private fun getCoffeeOrNull(localDate: LocalDate, coffeeType: CoffeeType): Int? {
        return _state.value.value[localDate]?.coffeeCountMap?.get(coffeeType)
    }

    private suspend fun putCoffeeCount(
        localDate: LocalDate,
        coffeeType: CoffeeType,
        count: Int
    ): DaysCoffeesState {
        val newValue = _state.value.value.toMutableMap().also {
            if (it[localDate] == null) {
                it[localDate] = DayCoffee()
            }
            val countMap: MutableMap<CoffeeType, Int> =
                it[localDate]!!.coffeeCountMap.toMutableMap()
            countMap[coffeeType] = count
            it[localDate] = DayCoffee(countMap)
        }
        //persist state here
        repository.createOrUpdate(newValue.toDaysCoffeesList())
        return repository.getAll().toState()
        /*return _state.value.copy(
            value = _state.value.value.toMutableMap().also{
                if (it[localDate]==null){
                    it[localDate] = DayCoffee()
                }
                val countMap: MutableMap<CoffeeType, Int> = it[localDate]!!.coffeeCountMap.toMutableMap()
                countMap[coffeeType] = count
                it[localDate] = DayCoffee(countMap)
            }
        )*/
    }

}

private fun List<DateCoffees>.toState(): DaysCoffeesState {
    return DaysCoffeesState(this.associateBy({ it: DateCoffees -> LocalDate.parse(it.date)},{it.dayCoffees.toDayCoffee()}))
}

private fun List<DbDayCoffee>.toDayCoffee(): DayCoffee {
    return DayCoffee(this.associateBy ({getByName(it.coffeeName)},{it.count }))
}

private fun Map<LocalDate, DayCoffee>.toDaysCoffeesList(): List<DateCoffees> {
    return this.toList().map { dayCoffees: Pair<LocalDate, DayCoffee> ->
        DateCoffees(
            date = dayCoffees.first.toString(),
            dayCoffees = dayCoffees.second.coffeeCountMap.toList()
                .map { dayCups: Pair<CoffeeType, Int> ->
                    DbDayCoffee(dayCups.first.name, dayCups.second)
                }
        )
    }
}


sealed class DaysCoffeesIntent {
    data class PlusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) :
        DaysCoffeesIntent()

    data class MinusCoffee(val localDate: LocalDate, val coffeeType: CoffeeType) :
        DaysCoffeesIntent()
}

data class DaysCoffeesState(override val value: Map<LocalDate, DayCoffee>) :
    State<Map<LocalDate, DayCoffee>>