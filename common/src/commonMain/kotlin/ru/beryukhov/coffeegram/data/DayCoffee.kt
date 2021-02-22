package ru.beryukhov.coffeegram.data

data class DayCoffee(val coffeeCountMap: Map<CoffeeType, Int> = mapOf(Cappucino to 0, Latte to 0)){
    fun getCoffeeType():  CoffeeType {
        return getIconType(coffeeCountMap)
    }
}

fun getIconType(coffeeCountMap: Map<CoffeeType, Int>): CoffeeType {
    //todo normal logic
    return when {
        coffeeCountMap[Cappucino]==0 && coffeeCountMap[Latte]!=0 -> Latte
        coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]==0 -> Cappucino
        else -> CommonCoffee
    }
}