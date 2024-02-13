@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ru.beryukhov.coffeegram.data.CoffeeType

@Composable
fun Image(coffeeType: CoffeeType, modifier: Modifier = Modifier) = androidx.compose.foundation.Image(
    painter = painterResource(resource = coffeeType.iconRes),
    "",
    modifier = modifier
)
