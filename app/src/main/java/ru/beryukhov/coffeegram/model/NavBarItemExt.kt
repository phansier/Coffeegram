package ru.beryukhov.coffeegram.model

import kotlinx.collections.immutable.persistentListOf
import kotlin.reflect.KClass

internal data class NavBarItemExt(
    val page: KClass<out NavigationState>,
    val intent: NavigationIntent,
    val navBarItem: NavBarItem
)
internal val calendarExt = NavBarItemExt(
    NavigationState.TablePage::class,
    NavigationIntent.ReturnToTablePage,
    calendar
)

internal val statsExt = NavBarItemExt(
    NavigationState.StatsPage::class,
    NavigationIntent.ToStatsPage,
    stats
)

internal val settingsExt = NavBarItemExt(
    NavigationState.SettingsPage::class,
    NavigationIntent.ToSettingsPage,
    settings
)
internal val specialtyExt = NavBarItemExt(
    NavigationState.MapPage::class,
    NavigationIntent.ToMapPage,
    specialty
)

internal fun getNavBarItemsOld(showMap: Boolean) =
    if (showMap) {
        persistentListOf(calendarExt, statsExt, settingsExt, specialtyExt)
    } else {
        persistentListOf(calendarExt, statsExt, settingsExt)
    }
