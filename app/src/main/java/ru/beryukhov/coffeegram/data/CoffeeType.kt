package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.beryukhov.coffeegram.R

enum class CoffeeType(
    @StringRes val nameId: Int,
    @DrawableRes val iconId: Int = R.drawable.coffee
) {
    Cappuccino(R.string.cappuccino, R.drawable.cappucino),
    Latte(R.string.latte, R.drawable.latte),
    Americano(R.string.americano),
}