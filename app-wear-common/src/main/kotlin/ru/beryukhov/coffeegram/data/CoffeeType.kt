package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.beryukhov.coffeegram.common.R

enum class CoffeeType(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int = R.drawable.coffee
) {
    Cappuccino(R.string.cappuccino, R.drawable.cappuccino),
    Latte(R.string.latte, R.drawable.latte),
    Americano(R.string.americano, R.drawable.americano),
    Macchiato(R.string.macchiato, R.drawable.macchiato),
    Glace(R.string.glace, R.drawable.glace),
    Frappe(R.string.frappe, R.drawable.latte),
    Espresso(R.string.espresso, R.drawable.espresso),
    Mocha(R.string.mocha, R.drawable.mocha),
    Fredo(R.string.fredo, R.drawable.fredo),
    Irish(R.string.irish, R.drawable.irish),
    Cocoa(R.string.cocoa, R.drawable.cocoa),
    Chocolate(R.string.chocolate, R.drawable.chocolate),
    // icons from here: https://www.freepik.com/free-vector/list-different-types-coffee_951047.htm
    // app logo is here: https://www.flaticon.com/free-icon/coffee-cup_766408
}
