package ru.beryukhov.coffeegram.data

import ru.beryukhov.coffeegram.R

data class DayCoffee(
    val coffeeCountMap: Map<CoffeeType, Int> = mapOf(Cappucino to 0, Latte to 0)
){
    fun getIconId():  Int? {
        //todo normal logic
        if (coffeeCountMap[Cappucino]==0 && coffeeCountMap[Latte]!=0) return Latte.iconId
        if (coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]==0) return Cappucino.iconId
        if (coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]!=0) return R.drawable.coffee
        return null
    }
}