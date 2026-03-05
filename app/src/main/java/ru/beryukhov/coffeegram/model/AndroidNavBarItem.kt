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

/**
 * Navigation bar item for Android app using Android string resources.
 */
data class NavBarItem(
    @StringRes val titleRes: Int,
    val icon: ImageVector
)

val calendar = NavBarItem(
    R.string.calendar,
    Icons.Default.Create
)

val stats = NavBarItem(
    R.string.stats,
    Icons.Default.Info
)

val settings = NavBarItem(
    R.string.settings,
    Icons.Default.Settings
)

val specialty = NavBarItem(
    R.string.map_short,
    Icons.Default.LocationOn
)

internal fun getAndroidNavBarItems(showMap: Boolean) =
    if (showMap) {
        persistentListOf(calendar, stats, specialty, settings)
    } else {
        persistentListOf(calendar, stats, settings)
    }
