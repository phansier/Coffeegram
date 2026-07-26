package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.union
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.map_long
import coffeegram.cmp_common.generated.resources.specialty_eyebrow
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.components.MapComponent

@Composable
expect fun MapScreen(
    component: MapComponent,
    modifier: Modifier = Modifier,
    showMarkerDescription: Boolean = true,
)

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun MapAppBar(component: MapComponent, modifier: Modifier = Modifier) {
    val hasUserLocation by component.hasUserLocation.collectAsState()
    AdaptiveTopAppBar(
        title = {
            TopBarTitle(
                title = stringResource(Res.string.map_long),
                eyebrow = if (hasUserLocation) stringResource(Res.string.specialty_eyebrow) else null,
            )
        },
        modifier = modifier,
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
    )
}
