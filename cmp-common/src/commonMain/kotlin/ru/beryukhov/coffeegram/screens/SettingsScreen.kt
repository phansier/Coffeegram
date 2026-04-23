package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.components.SettingsComponent
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.view.ThemeRadioButtonWithText
import ru.beryukhov.coffeegram.view.ThemeSwitchWithText

@Composable
fun SettingsScreen(
    component: SettingsComponent,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val themeState by component.models.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(
            rememberScrollState()
        )
    ) {
        Text(
            stringResource(Res.string.app_theme),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
        )
        DarkThemeRadioGroup(themeState.useDarkTheme, component)
        val scope = rememberCoroutineScope()

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
                onCheckedChange = {
                    if (it) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Now app theme will follow system theme",
                                actionLabel = "OK",
                            )
                        }
                    }
                    component.onSetDynamicTheme(it)
                },
                stringResource(Res.string.app_theme_dynamic)
            )
        }
        if (themeState.isSummer != null) {
            ThemeSwitchWithText(
                checked = themeState.isSummer == true,
                onCheckedChange = {
                    if (it) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Summer starting",
                                actionLabel = "OK",
                            )
                        }
                    }
                    component.onSetSummerTheme(it)
                },
                stringResource(Res.string.app_theme_summer)
            )
        }

        HorizontalDivider()

        if (component.onAndroidStartWearableActivity != null) {
            Button(
                onClick = { component.onAndroidStartWearableActivity?.invoke() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start Wearable Activity")
            }
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

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() = PreviewTheme {
    SettingsScreen(component = object : SettingsComponent {
        override val models: StateFlow<ThemeState> = MutableStateFlow(
            ThemeState(
                useDarkTheme = DarkThemeState.SYSTEM,
                isCupertino = true,
                isDynamic = null,
                isSummer = null
            )
        )
        override val onAndroidStartWearableActivity: (() -> Unit)? = null
        override val onAndroidIconChange: (isSummer: Boolean) -> Unit = { }


        override fun onSetSystemTheme() = Unit

        override fun onSetLightTheme() = Unit

        override fun onSetDarkTheme() = Unit

        override fun onSetCupertinoTheme(enabled: Boolean) = Unit

        override fun onSetDynamicTheme(enabled: Boolean) = Unit

        override fun onSetSummerTheme(enabled: Boolean) = Unit
    }
    )
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
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
    )
}
