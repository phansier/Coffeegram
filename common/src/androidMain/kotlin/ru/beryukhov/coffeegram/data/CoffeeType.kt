package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes
import ru.beryukhov.compose_common.R

actual abstract class CoffeeType constructor(
    actual val name: String,
    @DrawableRes val iconId: Int = R.drawable.coffee
)

actual object Cappucino : CoffeeType("Cappuccino", R.drawable.cappucino)
actual object Latte : CoffeeType("Latte", R.drawable.latte)
actual object CommonCoffee : CoffeeType("CommonCoffee")