package ru.beryukhov.coffeegram.wear

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.material3.ColorScheme
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.button
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.argb
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.Material3TileService
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.tile
import androidx.wear.tiles.timeline
import androidx.wear.tiles.timelineEntry
import kotlinx.coroutines.flow.first
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.durationUntilTomorrow

private const val ADD_COFFEE_CLICK_PREFIX = "add:"
private const val CLICK_ID_SEPARATOR = "@"
private const val TILE_COFFEE_COUNT = 2
private const val BUTTON_SPACING_DP = 4f

private data class TileCoffee(val type: CoffeeType, val label: String)

private val TileColorScheme = ColorScheme(
    primary = BrandColors.BROWN_500.argb,
    primaryDim = BrandColors.BROWN_700.argb,
    primaryContainer = BrandColors.BROWN_700.argb,
    onPrimary = BrandColors.BROWN_50.argb,
    onPrimaryContainer = BrandColors.BROWN_50.argb,
    secondary = BrandColors.TEAL_200.argb,
    onSecondary = BrandColors.BLACK.argb,
    tertiary = BrandColors.BROWN_200.argb,
    onTertiary = BrandColors.BLACK.argb,
)

class TodayTileService : Material3TileService(allowDynamicTheme = false, defaultColorScheme = TileColorScheme) {
    private val clickJournal by lazy { TileClickJournal(this) }

    override suspend fun MaterialScope.tileResponse(requestParams: TileRequest): Tile {
        val repository = coffeeRepository
        repository.refreshFromDataLayer()
        handleClick(requestParams.currentState.lastClickableId, repository)
        val dayCoffee = repository.todayCoffee.first()
        val layout = todayLayout(
            title = getString(R.string.tile_today_total, dayCoffee.total),
            coffees = dayCoffee.mostDrunk().map { (type, count) ->
                TileCoffee(type, getString(R.string.tile_coffee_count, type.localizedName.get(), count))
            },
            openAppLabel = getString(R.string.open_app),
        )
        return tile(timeline = timeline(timelineEntry(layout)), freshness = durationUntilTomorrow())
    }

    private fun handleClick(clickId: String, repository: WearCoffeeRepository) {
        if (!clickJournal.isNew(clickId)) return
        clickJournal.markHandled(clickId)
        clickId.toClickedCoffee()?.let { repository.change(it, +1) }
    }

    private fun MaterialScope.todayLayout(title: String, coffees: List<TileCoffee>, openAppLabel: String): LayoutElement {
        val renderId = System.currentTimeMillis().toString()
        return primaryLayout(
            titleSlot = { text(title.layoutString) },
            mainSlot = { coffeeButtons(coffees, renderId) },
            bottomSlot = { textEdgeButton(onClick = openApp()) { text(openAppLabel.layoutString) } },
        )
    }

    private fun MaterialScope.coffeeButtons(coffees: List<TileCoffee>, renderId: String): LayoutElement =
        LayoutElementBuilders.Column.Builder()
            .setWidth(expand())
            .apply {
                coffees.forEachIndexed { index, coffee ->
                    if (index > 0) addContent(LayoutElementBuilders.Spacer.Builder().setHeight(dp(BUTTON_SPACING_DP)).build())
                    addContent(coffeeButton(coffee, renderId))
                }
            }
            .build()

    private fun MaterialScope.coffeeButton(coffee: TileCoffee, renderId: String): LayoutElement =
        button(
            onClick = clickable(id = ADD_COFFEE_CLICK_PREFIX + coffee.type.dbKey + CLICK_ID_SEPARATOR + renderId),
            width = expand(),
            labelContent = { text(coffee.label.layoutString) },
        )

    private fun openApp(): Clickable =
        clickable(
            action = ActionBuilders.LaunchAction.Builder()
                .setAndroidActivity(
                    ActionBuilders.AndroidActivity.Builder()
                        .setPackageName(packageName)
                        .setClassName(WearActivity::class.java.name)
                        .build()
                )
                .build()
        )
}

private val DayCoffee.total: Int
    get() = coffeeCountMap.values.sum()

private fun DayCoffee.mostDrunk(): List<Pair<CoffeeType, Int>> =
    CoffeeTypes.entries
        .map { type -> type to (coffeeCountMap[type] ?: 0) }
        .sortedByDescending { (_, count) -> count }
        .take(TILE_COFFEE_COUNT)

private fun String.toClickedCoffee(): CoffeeType? {
    if (!startsWith(ADD_COFFEE_CLICK_PREFIX) || !contains(CLICK_ID_SEPARATOR)) return null
    val dbKey = removePrefix(ADD_COFFEE_CLICK_PREFIX).substringBefore(CLICK_ID_SEPARATOR)
    return CoffeeTypes.entries.find { it.dbKey == dbKey }
}
