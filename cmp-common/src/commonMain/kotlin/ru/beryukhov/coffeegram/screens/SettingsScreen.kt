package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.app_theme
import coffeegram.cmp_common.generated.resources.app_theme_cupertino
import coffeegram.cmp_common.generated.resources.app_theme_dark
import coffeegram.cmp_common.generated.resources.app_theme_light
import coffeegram.cmp_common.generated.resources.app_theme_system
import coffeegram.cmp_common.generated.resources.settings
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.components.SettingsComponent
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.view.ThemeRadioButtonWithText
import ru.beryukhov.coffeegram.view.ThemeSwitchWithText

@Composable
fun SettingsScreen(component: SettingsComponent, modifier: Modifier = Modifier) {
    val themeState by component.models.collectAsState()
    Column(modifier = modifier) {
        Text(
            stringResource(Res.string.app_theme),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = component::onSetSystemTheme,
            label = stringResource(Res.string.app_theme_system),
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = component::onSetLightTheme,
            label = stringResource(Res.string.app_theme_light),
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = component::onSetDarkTheme,
            label = stringResource(Res.string.app_theme_dark),
        )
        ThemeSwitchWithText(
            checked = themeState.isCupertino,
            onCheckedChange = component::onSetCupertinoTheme,
            label = stringResource(Res.string.app_theme_cupertino)
        )
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun SettingsAppBar(
    component: SettingsComponent,
    modifier: Modifier = Modifier
) {
    AdaptiveTopAppBar(
        title = { Text(stringResource(Res.string.settings)) },
        modifier = modifier,
    )
}
