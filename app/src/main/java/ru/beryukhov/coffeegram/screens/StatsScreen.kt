@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.components.StatsComponent
import ru.beryukhov.coffeegram.pages.CoffeeCharts

@Composable
fun StatsScreen(
    component: StatsComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.models.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        CoffeeCharts(coffeeState = state, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatsAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.stats)) },
        modifier = modifier
    )
}
