package ru.beryukhov.coffeegram.data

import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Call
import androidx.ui.material.icons.filled.Lock
import androidx.ui.material.icons.filled.ShoppingCart

abstract class CoffeeType(
    val name: String,
    val icon: VectorAsset = Icons.Default.Call
)

object Cappucino : CoffeeType("Cappucino", Icons.Default.ShoppingCart)
object Latte : CoffeeType("Latte", Icons.Default.Lock)

