package ru.beryukhov.coffeegram.data


actual data class DayCoffee actual constructor(actual val coffeeCountMap: Map<CoffeeType, Int>) {

    fun getIconId():  Int {
        return getIconType(coffeeCountMap).iconId
    }
}