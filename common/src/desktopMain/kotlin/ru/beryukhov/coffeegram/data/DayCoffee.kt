package ru.beryukhov.coffeegram.data

actual class DayCoffee actual constructor(actual val coffeeCountMap: Map<CoffeeType, Int>){

    fun getIconId():  String {
        return getIconType(coffeeCountMap).iconPath
    }
}