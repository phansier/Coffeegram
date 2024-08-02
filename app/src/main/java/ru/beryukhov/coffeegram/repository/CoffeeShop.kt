package ru.beryukhov.coffeegram.repository

data class CoffeeShop(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
) {
    override fun toString(): String {
        return "CoffeeShop(name='$name', lat=$latitude, lng=$longitude)"
    }
}
