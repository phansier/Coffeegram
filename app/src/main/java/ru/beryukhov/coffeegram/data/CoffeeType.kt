package ru.beryukhov.coffeegram.data

import androidx.annotation.DrawableRes

data class CoffeeType(
    @DrawableRes
    val image: Int,
    val name: String,
    val count: Int = 0
)