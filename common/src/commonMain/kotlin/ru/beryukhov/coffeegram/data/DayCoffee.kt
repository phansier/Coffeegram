package ru.beryukhov.coffeegram.data

expect class DayCoffee(coffeeCountMap: Map<CoffeeType, Int> = mapOf(Cappucino to 0, Latte to 0)){
    val coffeeCountMap: Map<CoffeeType, Int>
}

fun getIconType(coffeeCountMap: Map<CoffeeType, Int>): CoffeeType {
    //todo normal logic
    return when {
        coffeeCountMap[Cappucino]==0 && coffeeCountMap[Latte]!=0 -> Latte
        coffeeCountMap[Cappucino]!=0 && coffeeCountMap[Latte]==0 -> Cappucino
        else -> CommonCoffee
    }
}