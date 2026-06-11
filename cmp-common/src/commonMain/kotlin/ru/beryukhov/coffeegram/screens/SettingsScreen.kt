package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.app_theme
import coffeegram.cmp_common.generated.resources.app_theme_cupertino
import coffeegram.cmp_common.generated.resources.app_theme_dark
import coffeegram.cmp_common.generated.resources.app_theme_dynamic
import coffeegram.cmp_common.generated.resources.app_theme_light
import coffeegram.cmp_common.generated.resources.app_theme_summer
import coffeegram.cmp_common.generated.resources.app_theme_summer_snackbar
import coffeegram.cmp_common.generated.resources.app_theme_system
import coffeegram.cmp_common.generated.resources.app_theme_system_snackbar
import coffeegram.cmp_common.generated.resources.ok
import coffeegram.cmp_common.generated.resources.open_on_watch
import coffeegram.cmp_common.generated.resources.settings
import coffeegram.cmp_common.generated.resources.settings_device
import coffeegram.cmp_common.generated.resources.settings_style
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.components.SettingsComponent
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.view.ThemeRadioButtonWithText
import ru.beryukhov.coffeegram.view.ThemeSwitchWithText

private enum class SettingsCategory(val label: StringResource) {
    APPEARANCE(Res.string.app_theme),
    STYLE(Res.string.settings_style),
    DEVICE(Res.string.settings_device),
}

@Composable
fun SettingsScreen(
    component: SettingsComponent,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
) {
    val themeState by component.models.collectAsState()
    val wearableStarter by component.onAndroidStartWearableActivity.collectAsState()

    val hasStyle = themeState.isCupertino != null ||
        themeState.isDynamic != null ||
        themeState.isSummer != null
    val hasDevice = wearableStarter != null
    val categories = buildList {
        add(SettingsCategory.APPEARANCE)
        if (hasStyle) add(SettingsCategory.STYLE)
        if (hasDevice) add(SettingsCategory.DEVICE)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .consumeWindowInsets(contentPadding),
    ) {
        if (maxWidth >= WIDE_SCREEN_THRESHOLD) {
            val selectedName by component.selectedCategory.subscribeAsState()
            val selected = categories.firstOrNull { it.name == selectedName } ?: categories.first()

            Row(modifier = Modifier.fillMaxSize()) {
                SettingsCategoryList(
                    categories = categories,
                    selected = selected,
                    onSelect = { component.selectCategory(it.name) },
                    modifier = Modifier.width(240.dp).fillMaxHeight(),
                )
                VerticalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    SettingsDetail(selected, component, snackbarHostState, themeState, wearableStarter)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                categories.forEachIndexed { index, category ->
                    if (index > 0) HorizontalDivider()
                    SectionHeader(category.label)
                    SettingsDetail(category, component, snackbarHostState, themeState, wearableStarter)
                }
            }
        }
    }
}

@Composable
private fun SettingsCategoryList(
    categories: List<SettingsCategory>,
    selected: SettingsCategory,
    onSelect: (SettingsCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        categories.forEach { category ->
            Text(
                text = stringResource(category.label),
                style = typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(category) }
                    .background(
                        if (category == selected) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun SettingsDetail(
    category: SettingsCategory,
    component: SettingsComponent,
    snackbarHostState: SnackbarHostState,
    themeState: ThemeState,
    wearableStarter: (() -> Unit)?,
) {
    when (category) {
        SettingsCategory.APPEARANCE -> AppearanceSettings(themeState.useDarkTheme, component)
        SettingsCategory.STYLE -> StyleSettings(themeState, component, snackbarHostState)
        SettingsCategory.DEVICE -> DeviceSettings(wearableStarter)
    }
}

@Composable
private fun SectionHeader(label: StringResource) {
    Text(
        text = stringResource(label),
        style = typography.titleMedium,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun AppearanceSettings(
    useDarkTheme: DarkThemeState?,
    component: SettingsComponent,
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

@Composable
private fun StyleSettings(
    themeState: ThemeState,
    component: SettingsComponent,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    if (themeState.isCupertino != null) {
        ThemeSwitchWithText(
            checked = themeState.isCupertino == true,
            onCheckedChange = component::onSetCupertinoTheme,
            label = stringResource(Res.string.app_theme_cupertino),
        )
    }
    if (themeState.isDynamic != null) {
        val message = stringResource(Res.string.app_theme_system_snackbar)
        val actionLabel = stringResource(Res.string.ok)
        ThemeSwitchWithText(
            checked = themeState.isDynamic == true,
            onCheckedChange = {
                if (it) {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
                    }
                }
                component.onSetDynamicTheme(it)
            },
            stringResource(Res.string.app_theme_dynamic),
        )
    }
    if (themeState.isSummer != null) {
        val message = stringResource(Res.string.app_theme_summer_snackbar)
        val actionLabel = stringResource(Res.string.ok)
        ThemeSwitchWithText(
            checked = themeState.isSummer == true,
            onCheckedChange = {
                if (it) {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
                    }
                }
                component.onSetSummerTheme(it)
            },
            stringResource(Res.string.app_theme_summer),
        )
    }
}

@Composable
private fun DeviceSettings(wearableStarter: (() -> Unit)?) {
    if (wearableStarter != null) {
        Button(
            onClick = { wearableStarter.invoke() },
            modifier = Modifier.padding(16.dp),
        ) {
            Text(stringResource(Res.string.open_on_watch))
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() = PreviewTheme {
    SettingsScreen(
        component = previewComponent(),
        snackbarHostState = remember { SnackbarHostState() },
    )
}

private fun previewComponent() = object : SettingsComponent {
    override val models: StateFlow<ThemeState> = MutableStateFlow(
        ThemeState(
            useDarkTheme = DarkThemeState.SYSTEM,
            isCupertino = true,
            isDynamic = null,
            isSummer = null,
        )
    )
    override val onAndroidStartWearableActivity: StateFlow<(() -> Unit)?> = MutableStateFlow(null)
    override val onAndroidIconChange: (isSummer: Boolean) -> Unit = { }

    override val selectedCategory: Value<String> = MutableValue("")
    override fun selectCategory(id: String) = Unit

    override fun onSetSystemTheme() = Unit
    override fun onSetLightTheme() = Unit
    override fun onSetDarkTheme() = Unit
    override fun onSetCupertinoTheme(enabled: Boolean) = Unit
    override fun onSetDynamicTheme(enabled: Boolean) = Unit
    override fun onSetSummerTheme(enabled: Boolean) = Unit
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun SettingsAppBar(
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
    AdaptiveTopAppBar(
        title = { Text(stringResource(Res.string.settings)) },
        modifier = modifier,
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
    )
}
