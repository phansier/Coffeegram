package ru.beryukhov.coffeegram.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults.buttonColors
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.beryukhov.coffeegram.MainActivity
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.model.NavigationState.Companion.NAVIGATION_STATE_KEY
import ru.beryukhov.coffeegram.model.NavigationState.Companion.TODAYS_COFFEE_LIST
import ru.beryukhov.coffeegram.pages.AppWidgetViewModel
import ru.beryukhov.coffeegram.pages.AppWidgetViewModelImpl
import ru.beryukhov.coffeegram.pages.AppWidgetViewModelStub
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.HORIZONTAL_RECTANGLE
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.SMALL_SQUARE
import ru.beryukhov.coffeegram.common.R as common_R

class FirstGlanceWidget : GlanceAppWidget(errorUiLayout = R.layout.layout_widget_custom_error), KoinComponent {

    // override val sizeMode: SizeMode = SizeMode.Exact
    // for Android 12 Responsive layouts feature
    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(SMALL_SQUARE, HORIZONTAL_RECTANGLE, BIG_SQUARE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val viewModel: AppWidgetViewModelImpl by inject()
        provideContent {
            WidgetContent(viewModel)
        }
    }

    companion object {
        // TODO check this sizes after UI implementation
        // private val SMALL_SQUARE = DpSize(120.dp, 120.dp)
        // private val HORIZONTAL_RECTANGLE = DpSize(240.dp, 120.dp)
        // private val BIG_SQUARE = DpSize(240.dp, 240.dp)
        // private val SMALL_SQUARE = DpSize(50.dp, 50.dp)
        // private val HORIZONTAL_RECTANGLE = DpSize(100.dp, 50.dp)
        // private val BIG_SQUARE = DpSize(100.dp, 100.dp)
        internal val SMALL_SQUARE = DpSize(110.dp, 110.dp)
        internal val HORIZONTAL_RECTANGLE = DpSize(270.dp, 70.dp)
        internal val BIG_SQUARE = DpSize(270.dp, 140.dp)
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 100, heightDp = 100)
@Preview(widthDp = 260, heightDp = 60)
@Preview(widthDp = 280, heightDp = 150)
@Composable
internal fun WidgetContent(
    viewModel: AppWidgetViewModel = AppWidgetViewModelStub(),
) {
    val size = LocalSize.current
    GlanceTheme {
        Scaffold(
            backgroundColor = GlanceTheme.colors.widgetBackground,
            horizontalPadding = 0.dp,
        ) {
            when {
                size.width <= SMALL_SQUARE.width && size.height <= SMALL_SQUARE.height ->
                    SmallWidget(
                        count = viewModel.getCurrentDayCupsCount()
                    )

                size.width <= HORIZONTAL_RECTANGLE.width && size.height <= HORIZONTAL_RECTANGLE.height ->
                    HorizontalWidget(
                        coffeeTypeWithCount = viewModel.getCurrentDayMostPopularWithCount(),
                        increment = viewModel::currentDayIncrement,
                        decrement = viewModel::currentDayDecrement
                    )

                else -> BigWidget(
                    list = viewModel.getCurrentDayList(),
                    increment = viewModel::currentDayIncrement,
                    decrement = viewModel::currentDayDecrement
                )
            }
        }
    }
}

@Composable
private fun SmallWidget(
    modifier: GlanceModifier = GlanceModifier,
    count: Int = 5,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(
                        ActionParameters.Key<String>(NAVIGATION_STATE_KEY) to TODAYS_COFFEE_LIST
                    )
                )
            )
    ) {
        Image(
            provider = ImageProvider(resId = common_R.drawable.cappuccino),
            contentDescription = "",
            modifier = GlanceModifier
                .fillMaxSize()
                .height(64.dp)
                .width(64.dp)
        )
        Column(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxSize()
        ) {
            Text(
                "$count",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = ColorProvider(Color.Black),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                modifier = GlanceModifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HorizontalWidget(
    modifier: GlanceModifier = GlanceModifier.padding(24.dp).fillMaxSize(),
    coffeeTypeWithCount: CoffeeTypeWithCount = CoffeeTypeWithCount(CoffeeType.Cappuccino, 5),
    increment: (CoffeeType) -> Unit = {},
    decrement: (CoffeeType) -> Unit = {},
) {
    val padding = 16.dp
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            provider = ImageProvider(resId = coffeeTypeWithCount.coffee.iconId),
            contentDescription = "",
            modifier = GlanceModifier
                .fillMaxHeight()
                .size(48.dp)
        )
        Spacer(GlanceModifier.width(16.dp))
        Text(
            text = LocalContext.current.getString(coffeeTypeWithCount.coffee.nameId),
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.primary,
            ),
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
        )
        Spacer(GlanceModifier.width(padding))

        val isReduceCountAllowed = coffeeTypeWithCount.count > 0
        val buttonColors = buttonColors(
            backgroundColor = GlanceTheme.colors.background,
            contentColor = GlanceTheme.colors.primary
        )
        Button(
            text = "-",
            enabled = isReduceCountAllowed,
            modifier = GlanceModifier.width(32.dp).height(48.dp).padding(0.dp),
            colors = buttonColors,
            onClick = action {
                decrement(coffeeTypeWithCount.coffee)
            }
        )
        Spacer(GlanceModifier.width(padding))

        Text(
            "${coffeeTypeWithCount.count}",
            style = TextStyle(
                fontSize = 20.sp,
                color = GlanceTheme.colors.secondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),

            )
        Spacer(GlanceModifier.width(padding))
        Button(
            text = "+",
            modifier = GlanceModifier.width(32.dp).height(48.dp).padding(0.dp),
            colors = buttonColors,
            onClick = action {
                increment(coffeeTypeWithCount.coffee)
            }
        )
    }
}

// todo actual data
@Composable
private fun BigWidget(
    modifier: GlanceModifier = GlanceModifier,
    list: PersistentList<CoffeeTypeWithCount> = mockList,
    increment: (CoffeeType) -> Unit = {},
    decrement: (CoffeeType) -> Unit = {},
) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        items(list) {
            HorizontalWidget(
                modifier = GlanceModifier.padding(24.dp).fillMaxWidth().height(100.dp),
                coffeeTypeWithCount = it,
                increment = increment,
                decrement = decrement
            )
        }
    }
}

private val mockList: PersistentList<CoffeeTypeWithCount> = persistentListOf(
    CoffeeTypeWithCount(CoffeeType.Cappuccino, 5),
    CoffeeTypeWithCount(CoffeeType.Espresso, 3),
    CoffeeTypeWithCount(CoffeeType.Latte, 2),
)
