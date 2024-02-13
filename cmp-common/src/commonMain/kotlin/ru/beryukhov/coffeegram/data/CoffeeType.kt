@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.data

import coffeegram.`cmp-common`.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

fun coffeeTypeValues() = listOf(Latte, Cappuccino, Americano, CommonCoffee) // todo replace it by some enum
fun coffeeTypeValueOf(name: StringResource): CoffeeType {
    return when (name) {
        Latte.name -> Latte
        Cappuccino.name -> Cappuccino
        Americano.name -> Americano
        else -> CommonCoffee
    }
}

suspend fun coffeeTypeValueOf(name: String): CoffeeType {
    return when (name) {
        getString(Latte.name) -> Latte
        getString(Cappuccino.name) -> Cappuccino
        getString(Americano.name) -> Americano
        else -> CommonCoffee
    }
}

abstract class CoffeeType constructor(
    val name: StringResource,
    val iconRes: DrawableResource = Res.drawable.coffee
)

object Cappuccino : CoffeeType(Res.string.cappuccino, Res.drawable.ic_cappuccino)
object Latte : CoffeeType(Res.string.latte, Res.drawable.ic_latte)
object Americano : CoffeeType(Res.string.americano, Res.drawable.coffee)
object CommonCoffee : CoffeeType(Res.string.common)
