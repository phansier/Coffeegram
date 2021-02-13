package ru.beryukhov.coffeegram.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.beryukhov.coffeegram.data.CoffeeType

@Composable
actual fun Image(coffeeType: CoffeeType, modifier: Modifier) = androidx.compose.foundation.Image(
    painter = painterResource(id = coffeeType.iconId),
    "",
    modifier = modifier
)