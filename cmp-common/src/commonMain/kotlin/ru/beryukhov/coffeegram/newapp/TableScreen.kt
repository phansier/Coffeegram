package ru.beryukhov.coffeegram.newapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TableScreen(
    component: TableComponent,
) {
    Text("Table")
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TableAppBar(
    component: TableComponent,
) {
    AdaptiveTopAppBar(
        title = {
            Text("Table")
        },
    )
}
