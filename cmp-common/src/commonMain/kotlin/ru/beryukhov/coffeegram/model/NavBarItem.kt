package ru.beryukhov.coffeegram.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.calendar
import coffeegram.cmp_common.generated.resources.map_short
import coffeegram.cmp_common.generated.resources.settings
import coffeegram.cmp_common.generated.resources.stats
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class NavBarItem(
    val title: StringResource,
    val icon: ImageVector
)

val calendar = NavBarItem(
    Res.string.calendar,
    Icons.Default.Create
)

val stats = NavBarItem(
    Res.string.stats,
    Icons.Default.Info
)

val settings = NavBarItem(
    Res.string.settings,
    Icons.Default.Settings
)
 val specialty = NavBarItem(
     Res.string.map_short,
     Icons.Default.LocationOn
 )

internal fun getNavBarItems() = persistentListOf(calendar, settings)

@Composable
@Suppress("ModifierMissing")
fun NavBarItem.Text() = Text(stringResource(title))
