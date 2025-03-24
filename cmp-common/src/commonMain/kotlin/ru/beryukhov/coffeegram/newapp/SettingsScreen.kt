package ru.beryukhov.coffeegram.newapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi

@Composable
fun SettingsScreen(component: SettingsComponent,) {
    Text("Settings")
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun SettingsAppBar(
    component: SettingsComponent,
) {
    AdaptiveTopAppBar(
        title = {
            Text("Settings")
        },
    )
}
