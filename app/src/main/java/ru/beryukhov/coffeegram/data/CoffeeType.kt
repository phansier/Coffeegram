package ru.beryukhov.coffeegram.data

import androidx.compose.Composable
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.res.vectorResource
import ru.beryukhov.coffeegram.R

sealed class CoffeeType(
    val name: String
) {
    @Composable
    abstract fun icon(): VectorAsset
}

object Cappucino : CoffeeType("Cappucino") {
    @Composable
    override fun icon() = vectorResource(R.drawable.cappucino)
}

object Latte : CoffeeType("Latte") {
    @Composable
    override fun icon() = vectorResource(R.drawable.latte)
}

