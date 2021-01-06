package ru.beryukhov.coffeegram.data

expect abstract class CoffeeType {
    val name: String
}
expect object Latte : CoffeeType
expect object Cappucino : CoffeeType
expect object CommonCoffee : CoffeeType