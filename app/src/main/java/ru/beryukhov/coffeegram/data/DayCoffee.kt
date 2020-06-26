package ru.beryukhov.coffeegram.data

import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.collectAsState
import androidx.ui.graphics.vector.VectorAsset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate

typealias DaysCoffeesFlow = MutableStateFlow<DaysCoffees>
typealias DaysCoffees = Map<LocalDate, DayCoffee>

data class DayCoffee(
    val coffeeCountMap: Map<CoffeeType, Int> = mapOf(Cappucino to 0, Latte to 0)
){
    fun getVector(): VectorAsset? {
        //todo normal logic
        if (coffeeCountMap[Cappucino]==0 && coffeeCountMap[Latte]!=0) return Latte.icon
        if (coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]==0) return Cappucino.icon
        return null
    }
}

data class DayCoffeeFlow(
    val parentFlow: DaysCoffeesFlow,
    val date: LocalDate
) {
    fun set(value: DayCoffee) {
        parentFlow.value = parentFlow.value.toMutableMap().also { it[date] = value }
    }

    @Composable
    fun getState(): State<DayCoffee> {
        return parentFlow.map { it[date] ?: DayCoffee() }.collectAsState(initial = DayCoffee())
    }

    val value: DayCoffee get() = parentFlow.value[date] ?: DayCoffee()
}

data class IntFlow(
    val parentFlow: DayCoffeeFlow,
    val coffeeType: CoffeeType
) {
    fun set(value: Int) {
        parentFlow.set(
            DayCoffee(
                parentFlow.value.coffeeCountMap.toMutableMap().also { it[coffeeType] = value })
        )
    }

    @Composable
    fun getState(): State<Int> {
        return parentFlow.parentFlow.map { it[parentFlow.date] ?: DayCoffee() }
            .map { it.coffeeCountMap[coffeeType] ?: 0 }.collectAsState(
                initial = 0
            )
    }
}