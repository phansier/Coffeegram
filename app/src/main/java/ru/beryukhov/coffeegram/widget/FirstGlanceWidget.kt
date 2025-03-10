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
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.BIG_SQUARE
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.HORIZONTAL_RECTANGLE
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
        // https://developer.android.com/develop/ui/views/appwidgets/layouts#anatomy_determining_size
        internal val SMALL_SQUARE = DpSize(50.dp, 50.dp) // 1x1
        internal val HORIZONTAL_RECTANGLE = DpSize(200.dp, 80.dp) // 3x1
        internal val BIG_SQUARE = DpSize(270.dp, 140.dp) // 3x3
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 50, heightDp = 50)
@Preview(widthDp = 200, heightDp = 100)
@Preview(widthDp = 300, heightDp = 300)
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
                size.width < HORIZONTAL_RECTANGLE.width ->
                    SmallWidget(
                        count = viewModel.getCurrentDayCupsCount()
                    )

                size.height < BIG_SQUARE.height ->
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
    count: Int,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .clickable(openAppAction)
    ) {
        Image(
            provider = ImageProvider(resId = common_R.drawable.cappuccino),
            contentDescription = "",
            modifier = GlanceModifier
                .fillMaxSize()
                .size(36.dp)
        )
        Text(
            "$count",
            style = TextStyle(
                fontSize = 16.sp,
                color = ColorProvider(Color.Black),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(end = 6.dp)
        )
    }
}

private val openAppAction = actionStartActivity<MainActivity>(
    actionParametersOf(
        ActionParameters.Key<String>(NAVIGATION_STATE_KEY) to TODAYS_COFFEE_LIST
    )
)

@Composable
private fun HorizontalWidget(
    coffeeTypeWithCount: CoffeeTypeWithCount,
    modifier: GlanceModifier = GlanceModifier.padding(16.dp).fillMaxSize(),
    increment: (CoffeeType) -> Unit = {},
    decrement: (CoffeeType) -> Unit = {},
) {
    val padding = 6.dp
    Row(
        modifier = modifier.clickable(openAppAction),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val isReduceCountAllowed = coffeeTypeWithCount.count > 0
        val buttonColors = buttonColors(
            backgroundColor = GlanceTheme.colors.background,
            contentColor = GlanceTheme.colors.primary
        )
        Button(
            text = "-",
            enabled = isReduceCountAllowed,
            modifier = GlanceModifier.size(32.dp).padding(0.dp),
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
            modifier = GlanceModifier.size(32.dp).padding(0.dp),
            colors = buttonColors,
            onClick = action {
                increment(coffeeTypeWithCount.coffee)
            }
        )
        Spacer(GlanceModifier.width(padding))
        Text(
            text = LocalContext.current.getString(coffeeTypeWithCount.coffee.nameId),
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.primary,
            ),
            modifier = GlanceModifier

        )
        Spacer(GlanceModifier.width(padding).defaultWeight())
        Image(
            provider = ImageProvider(resId = coffeeTypeWithCount.coffee.iconId),
            contentDescription = "",
            modifier = GlanceModifier
                .fillMaxHeight()
                .size(36.dp)
        )
    }
}

@Composable
private fun BigWidget(
    list: PersistentList<CoffeeTypeWithCount>,
    modifier: GlanceModifier = GlanceModifier,
    increment: (CoffeeType) -> Unit = {},
    decrement: (CoffeeType) -> Unit = {},
) {
    LazyColumn(modifier = modifier.padding(vertical = 16.dp).fillMaxSize()) {
        items(list) {
            HorizontalWidget(
                modifier = GlanceModifier.padding(16.dp).fillMaxWidth().height(72.dp),
                coffeeTypeWithCount = it,
                increment = increment,
                decrement = decrement
            )
        }
    }
}
