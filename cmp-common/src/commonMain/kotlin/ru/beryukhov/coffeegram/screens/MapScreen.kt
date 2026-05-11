package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.map_long
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.components.MapComponent

@Composable
expect fun MapScreen(
    component: MapComponent,
    modifier: Modifier = Modifier,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(Res.string.map_long)) },
        modifier = modifier,
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
    )
}
