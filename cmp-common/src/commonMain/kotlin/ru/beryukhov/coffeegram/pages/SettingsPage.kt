@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.app_theme
import coffeegram.cmp_common.generated.resources.app_theme_cupertino
import coffeegram.cmp_common.generated.resources.app_theme_dark
import coffeegram.cmp_common.generated.resources.app_theme_light
import coffeegram.cmp_common.generated.resources.app_theme_system
import coffeegram.cmp_common.generated.resources.settings
import com.slapps.cupertino.adaptive.AdaptiveSwitch
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

@Composable
fun ColumnScope.SettingsPage(
    themeStore: ThemeStore,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.weight(1f)) {
        Text(
            stringResource(Res.string.app_theme),
            style = typography.titleMedium,
            modifier = Modifier.absolutePadding(left = 24.dp, top = 16.dp)
        )
        val themeState: ThemeState by themeStore.state.collectAsState()
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = { themeStore.newIntent(ThemeIntent.SetSystemIntent) },
            label = stringResource(Res.string.app_theme_system),
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = { themeStore.newIntent(ThemeIntent.SetLightIntent) },
            label = stringResource(Res.string.app_theme_light),
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = { themeStore.newIntent(ThemeIntent.SetDarkIntent) },
            label = stringResource(Res.string.app_theme_dark),
        )
        val scope = rememberCoroutineScope()
        ThemeSwitchWithText(
            checked = themeState.isCupertino,
            onCheckedChange = {
                if (it) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Now app will use cupertino theme",
                            actionLabel = "OK",
                        )
                    }
                }
                themeStore.newIntent(
                    if (it) ThemeIntent.SetCupertinoIntent else ThemeIntent.UnSetCupertinoIntent
                )
            },
            label = stringResource(Res.string.app_theme_cupertino)
        )
    }
}

@Composable
fun SettingsAppBar(modifier: Modifier = Modifier) {
    AdaptiveTopAppBar(modifier = modifier, title = { Text(stringResource(Res.string.settings)) })
}

@Composable
fun ThemeRadioButtonWithText(
    selected: Boolean,
    onClick: (() -> Unit)?,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        RadioButton(selected = selected, onClick = onClick, modifier = Modifier.align(CenterVertically))
        Text(text = label, modifier = Modifier.align(CenterVertically))
    }
}

@Composable
fun ThemeSwitchWithText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(24.dp)) {
        Text(text = label, modifier = Modifier.weight(1f).align(CenterVertically))
        AdaptiveSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.align(CenterVertically)
        )
    }
}
