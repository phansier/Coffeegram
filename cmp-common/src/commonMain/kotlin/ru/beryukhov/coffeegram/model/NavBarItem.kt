package ru.beryukhov.coffeegram.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.calendar
import coffeegram.cmp_common.generated.resources.settings
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

internal data class NavBarItem(
    val title: StringResource,
    val icon: ImageVector
)
private val calendar = NavBarItem(
    Res.string.calendar,
    Icons.Default.Create
)

// private val stats = NavBarItem(
//     Res.string.stats,
//     Icons.Default.Info
// )

private val settings = NavBarItem(
    Res.string.settings,
    Icons.Default.Settings
)
// private val specialty = NavBarItem(
//     Res.string.map_short,
//     Icons.Default.LocationOn
// )

internal fun getNavBarItems() = persistentListOf(calendar, settings)
