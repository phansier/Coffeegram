package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.Strings
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

@Composable
fun ColumnScope.SettingsPage(
    themeStore: ThemeStore,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.weight(1f)) {
        Text(
            Strings.app_theme,
            style = typography.h6,
            modifier = Modifier.absolutePadding(left = 24.dp, top = 16.dp)
        )
        val themeState: ThemeState by themeStore.state.collectAsState()
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = { themeStore.newIntent(ThemeIntent.SetSystemIntent) },
            Strings.app_theme_system
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = { themeStore.newIntent(ThemeIntent.SetLightIntent) },
            Strings.app_theme_light
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = { themeStore.newIntent(ThemeIntent.SetDarkIntent) },
            Strings.app_theme_dark
        )
    }
}

@Composable
fun SettingsAppBar(modifier: Modifier = Modifier) {
    TopAppBar(modifier = modifier, title = { Text(Strings.settings) })
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
fun ThemeCheckBoxWithText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.align(CenterVertically))
        Text(text = label, modifier = Modifier.align(CenterVertically))
    }
}
