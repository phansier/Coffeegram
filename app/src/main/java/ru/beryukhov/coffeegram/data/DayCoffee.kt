package ru.beryukhov.coffeegram.data

import androidx.compose.Composable
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.res.vectorResource
import ru.beryukhov.coffeegram.R

data class DayCoffee(
    val coffeeCountMap: Map<CoffeeType, Int> = mapOf(Cappucino to 0, Latte to 0)
){
    @Composable
    fun getVector(): VectorAsset? {
        //todo normal logic
        if (coffeeCountMap[Cappucino]==0 && coffeeCountMap[Latte]!=0) return Latte.icon()
        if (coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]==0) return Cappucino.icon()
        if (coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]!=0) return vectorResource(R.drawable.coffee)
        return null
    }
}