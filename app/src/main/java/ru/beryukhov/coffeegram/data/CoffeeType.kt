package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes
import ru.beryukhov.coffeegram.R

abstract class CoffeeType(
    val name: String,
    @DrawableRes val iconId: Int = R.drawable.coffee
)

object Cappucino : CoffeeType("Cappuccino", R.drawable.cappucino)
object Latte : CoffeeType("Latte", R.drawable.latte)

