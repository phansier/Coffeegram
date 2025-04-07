package ru.beryukhov.coffeegram.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.persistentListOf
import ru.beryukhov.coffeegram.R
import kotlin.reflect.KClass

internal data class NavBarItem(
    val page: KClass<out NavigationState>,
    val intent: NavigationIntent,
    @StringRes val title: Int,
    val icon: ImageVector
)
private val calendar = NavBarItem(
    NavigationState.TablePage::class,
    NavigationIntent.ReturnToTablePage,
    R.string.calendar,
    Icons.Default.Create
)

private val stats = NavBarItem(
    NavigationState.StatsPage::class,
    NavigationIntent.ToStatsPage,
    R.string.stats,
    Icons.Default.Info
)

private val settings = NavBarItem(
    NavigationState.SettingsPage::class,
    NavigationIntent.ToSettingsPage,
    R.string.settings,
    Icons.Default.Settings
)
private val specialty = NavBarItem(
    NavigationState.MapPage::class,
    NavigationIntent.ToMapPage,
    R.string.map_short,
    Icons.Default.LocationOn
)

internal fun getNavBarItemsOld(showMap: Boolean) =
    if (showMap) {
        persistentListOf(calendar, stats, settings, specialty)
    } else {
        persistentListOf(calendar, stats, settings)
    }
