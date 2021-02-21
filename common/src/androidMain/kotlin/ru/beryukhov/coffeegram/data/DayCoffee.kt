package ru.beryukhov.coffeegram.data


actual data class DayCoffee actual constructor(actual val coffeeCountMap: Map<CoffeeType, Int>) {

    fun getCoffeeType():  CoffeeType {
        return getIconType(coffeeCountMap)
    }
}