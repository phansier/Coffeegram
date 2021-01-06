package ru.beryukhov.coffeegram.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import ru.beryukhov.coffeegram.data.CoffeeType

@Composable
actual fun Image(coffeeType: CoffeeType, modifier: Modifier) = androidx.compose.foundation.Image(
    imageVector = vectorResource(id = coffeeType.iconId),
    modifier = modifier
)