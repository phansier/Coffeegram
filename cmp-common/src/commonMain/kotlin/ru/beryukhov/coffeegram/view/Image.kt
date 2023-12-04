package ru.beryukhov.coffeegram.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.beryukhov.coffeegram.data.CoffeeType

@Composable
fun Image(coffeeType: CoffeeType, modifier: Modifier = Modifier) = androidx.compose.foundation.Image(
    painter = painterResource(res = coffeeType.iconRes),
    "",
    modifier = modifier
)
