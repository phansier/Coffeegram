package ru.beryukhov.coffeegram.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.beryukhov.coffeegram.R

/**
 * Navigation bar item for Android app using Android string resources.
 */
data class AndroidNavBarItem(
    @field:StringRes val titleRes: Int,
    val icon: ImageVector
)

val calendar = AndroidNavBarItem(
    titleRes = R.string.calendar,
    icon = Icons.Default.Create
)

val stats = AndroidNavBarItem(
    R.string.stats,
    Icons.Default.Info
)

val settings = AndroidNavBarItem(
    R.string.settings,
    Icons.Default.Settings
)

val specialty = AndroidNavBarItem(
    R.string.map_short,
    Icons.Default.LocationOn
)

internal fun getAndroidNavBarItems(showMap: Boolean): PersistentList<AndroidNavBarItem> =
    if (showMap) {
        persistentListOf(calendar, stats, specialty, settings)
    } else {
        persistentListOf(calendar, stats, settings)
    }
