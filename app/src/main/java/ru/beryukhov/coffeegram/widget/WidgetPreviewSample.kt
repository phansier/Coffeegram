package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.CoffeeTypes
import kotlin.time.Duration.Companion.seconds

private val STORED_DATA_TIMEOUT = 1.seconds

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

/**
 * The store starts empty and loads from storage asynchronously, so wait a bounded time for a day
 * with any coffee in it. Null covers all three ways that can come up empty - the wait timed out,
 * the flow ended without a match, or nothing was logged today - and a sample reads better in the
 * picker than an empty widget.
 */
internal suspend fun WidgetDataBridge.previewCoffees(): PersistentList<WidgetCoffee> =
    withTimeoutOrNull(STORED_DATA_TIMEOUT) {
        getCurrentDayList().firstOrNull { day -> day.any { it.count > 0 } }
    } ?: widgetPreviewCounts.toWidgetCoffees()
