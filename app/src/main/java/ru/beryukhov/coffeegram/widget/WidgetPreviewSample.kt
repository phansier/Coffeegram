package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes

internal val widgetPreviewCounts: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeTypes.Espresso, 3),
    CoffeeTypeWithCount(CoffeeTypes.Macchiato, 2),
)
