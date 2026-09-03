package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes

/**
 * Ordered by count descending, the way [DefaultWidgetDataBridge] sorts a real day, and long
 * enough to fill the tallest preview the widget picker renders.
 */
internal val widgetPreviewCounts: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeTypes.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeTypes.Latte, 4),
    CoffeeTypeWithCount(CoffeeTypes.Espresso, 3),
    CoffeeTypeWithCount(CoffeeTypes.Americano, 2),
    CoffeeTypeWithCount(CoffeeTypes.Macchiato, 2),
    CoffeeTypeWithCount(CoffeeTypes.Glace, 1),
    CoffeeTypeWithCount(CoffeeTypes.Mocha, 1),
    CoffeeTypeWithCount(CoffeeTypes.Irish, 1),
)
