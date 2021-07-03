package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes
import ru.beryukhov.coffeegram.R
import java.util.*

abstract class CoffeeType(
    val name: String,
    @DrawableRes val iconId: Int = R.drawable.coffee
)

object Cappucino : CoffeeType("Cappuccino", R.drawable.cappucino)
object Latte : CoffeeType("Latte", R.drawable.latte)

fun getByName(name: String): CoffeeType {
    return when (name.lowercase()) {
        Cappucino.name.lowercase() -> Cappucino
        Latte.name.lowercase() -> Latte
        else -> object : CoffeeType(name = name) {}
    }
}
