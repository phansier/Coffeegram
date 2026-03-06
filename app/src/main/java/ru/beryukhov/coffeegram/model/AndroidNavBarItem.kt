package ru.beryukhov.coffeegram.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal fun getAndroidNavBarItems(showMap: Boolean): PersistentList<NavBarItem> =
    if (showMap) {
        persistentListOf(calendar, stats, specialty, settings)
    } else {
        persistentListOf(calendar, stats, settings)
    }
