package ru.beryukhov.coffeegram.newapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TableScreen(
    component: TableComponent,
    modifier: Modifier = Modifier
) {
    Text("Table", modifier = modifier)
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TableAppBar(
    component: TableComponent,
    modifier: Modifier = Modifier
) {
    AdaptiveTopAppBar(
        title = { Text("Table") },
        modifier = modifier,
    )
}
