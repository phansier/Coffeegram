@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.BuildConfig
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.changeIcon
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage

@Preview
@Composable
internal fun SettingsPagePreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    CoffeegramTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                SettingsPage(
                    themeStore = getThemeStoreStub(LocalContext.current),
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun ColumnScope.SettingsPage(
    themeStore: ThemeStore,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    startWearableActivity: () -> Unit = {}
) {
    Column(modifier = modifier.weight(1f)) {
        Text(
            stringResource(R.string.app_theme),
            style = typography.titleMedium,
            modifier = Modifier.absolutePadding(left = 24.dp, top = 16.dp)
        )
        val themeState: ThemeState by themeStore.state.collectAsState()
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = { themeStore.newIntent(ThemeIntent.SetSystemIntent) },
            stringResource(R.string.app_theme_system)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = { themeStore.newIntent(ThemeIntent.SetLightIntent) },
            stringResource(R.string.app_theme_light)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = { themeStore.newIntent(ThemeIntent.SetDarkIntent) },
            stringResource(R.string.app_theme_dark)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scope = rememberCoroutineScope()
            ThemeCheckBoxWithText(
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
                    themeStore.newIntent(ThemeIntent.SetDynamicIntent(it))
                },
                stringResource(R.string.app_theme_dynamic)
            )
        }

        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        ThemeCheckBoxWithText(
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
                changeIcon(context, it)
                themeStore.newIntent(ThemeIntent.SetSummerIntent(it))
            },
            stringResource(R.string.app_theme_summer)
        )
        HorizontalDivider()
        if (BuildConfig.DEBUG) {
            Button(onClick = { startWearableActivity() }, modifier = Modifier.padding(16.dp)) {
                Text("Start Wearable Activity")
            }
        }
    }
}

@Composable
fun SettingsAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        modifier = modifier
    )
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

private fun getThemeStoreStub(context: Context) = ThemeStore(ThemeInMemoryStorage())
