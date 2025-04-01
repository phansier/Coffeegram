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
import coffeegram.cmp_common.generated.resources.app_theme_dynamic
import coffeegram.cmp_common.generated.resources.app_theme_light
import coffeegram.cmp_common.generated.resources.app_theme_summer
import coffeegram.cmp_common.generated.resources.app_theme_system
import coffeegram.cmp_common.generated.resources.settings
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.components.SettingsComponent
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
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
        DarkThemeRadioGroup(themeState.useDarkTheme, component)

        if (themeState.isCupertino != null) {
            ThemeSwitchWithText(
                checked = themeState.isCupertino == true,
                onCheckedChange = component::onSetCupertinoTheme,
                label = stringResource(Res.string.app_theme_cupertino)
            )
        }
        if (themeState.isDynamic != null) {
            ThemeSwitchWithText(
                checked = themeState.isDynamic == true,
                onCheckedChange = component::onSetDynamicTheme,
                stringResource(Res.string.app_theme_dynamic)
            )
        }
        if (themeState.isSummer != null) {
            ThemeSwitchWithText(
                checked = themeState.isSummer == true,
                onCheckedChange = component::onSetSummerTheme,
                stringResource(Res.string.app_theme_summer)
            )
        }
    }
}

@Composable
private fun DarkThemeRadioGroup(
    useDarkTheme: DarkThemeState?,
    component: SettingsComponent
) {
    ThemeRadioButtonWithText(
        selected = useDarkTheme == DarkThemeState.SYSTEM,
        onClick = component::onSetSystemTheme,
        label = stringResource(Res.string.app_theme_system),
    )
    ThemeRadioButtonWithText(
        selected = useDarkTheme == DarkThemeState.LIGHT,
        onClick = component::onSetLightTheme,
        label = stringResource(Res.string.app_theme_light),
    )
    ThemeRadioButtonWithText(
        selected = useDarkTheme == DarkThemeState.DARK,
        onClick = component::onSetDarkTheme,
        label = stringResource(Res.string.app_theme_dark),
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    CoffeegramTheme() {
        SettingsScreen(component = object : SettingsComponent {
            override val models: StateFlow<ThemeState> = MutableStateFlow(
                ThemeState(
                    useDarkTheme = DarkThemeState.SYSTEM,
                    isCupertino = true,
                    isDynamic = null,
                    isSummer = null
                )
            )

            override fun onSetSystemTheme() = Unit

            override fun onSetLightTheme() = Unit

            override fun onSetDarkTheme() = Unit

            override fun onSetCupertinoTheme(enabled: Boolean) = Unit

            override fun onSetDynamicTheme(enabled: Boolean) = Unit

            override fun onSetSummerTheme(enabled: Boolean) = Unit
        })
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
