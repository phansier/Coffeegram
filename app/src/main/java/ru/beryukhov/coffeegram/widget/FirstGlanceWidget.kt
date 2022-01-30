package ru.beryukhov.coffeegram.widget;


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.background
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import ru.beryukhov.coffeegram.MainActivity
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.brown500
import ru.beryukhov.coffeegram.data.CoffeeType


class FirstGlanceWidget : GlanceAppWidget(errorUiLayout = R.layout.layout_widget_custom_error) {

    companion object {
        //TODO check this sizes after UI implementation
//        private val SMALL_SQUARE = DpSize(120.dp, 120.dp)
//        private val HORIZONTAL_RECTANGLE = DpSize(240.dp, 120.dp)
//        private val BIG_SQUARE = DpSize(240.dp, 240.dp)
//        private val SMALL_SQUARE = DpSize(50.dp, 50.dp)
//        private val HORIZONTAL_RECTANGLE = DpSize(100.dp, 50.dp)
//        private val BIG_SQUARE = DpSize(100.dp, 100.dp)
        private val SMALL_SQUARE = DpSize(110.dp, 110.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(270.dp, 70.dp)
        private val BIG_SQUARE = DpSize(270.dp, 140.dp)
    }

    //override val sizeMode: SizeMode = SizeMode.Exact
    //for Android 12 Responsive layouts feature
    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(SMALL_SQUARE, HORIZONTAL_RECTANGLE, BIG_SQUARE))


    @Composable
    override fun Content() {
        val size = LocalSize.current

        /* // test sizes of widgets
        Text(
            text = "W${size.width.value.toInt()} H${size.height.value.toInt()}",
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        )*/
        Box(
            modifier = GlanceModifier.background(
                day = brown500,
                night = Color.DarkGray
            )
        ) {

            when {
                size.width <= SMALL_SQUARE.width && size.height <= SMALL_SQUARE.height -> SmallWidget()
                size.width <= HORIZONTAL_RECTANGLE.width && size.height <= HORIZONTAL_RECTANGLE.height -> HorizontalWidget()
                else -> BigWidget()
            }
        }
    }

    @Composable
    fun SmallWidget(count: Int = 5) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(
                    actionStartActivity<MainActivity>(
                        /*todo add parameters to open daycoffees list*/
                    )
                )
        ) {
            Image(
                provider = AndroidResourceImageProvider(resId = R.drawable.cappuccino),
                contentDescription = "",
                modifier = GlanceModifier
                    .fillMaxSize()
                    .height(64.dp)
                    .width(64.dp)
            )
            //workaround for aligning text in center by vertical
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
    fun HorizontalWidget(
        coffeeType: CoffeeType = CoffeeType.Cappuccino,
        modifier: GlanceModifier = GlanceModifier.padding(24.dp).fillMaxSize()
    ) {
        Row(
            modifier = modifier
        ) {
            Column(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
                    .fillMaxHeight()
            ) {
                Image(
                    provider = AndroidResourceImageProvider(resId = coffeeType.iconId),
                    contentDescription = "",
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .size(48.dp)
                )
            }
            Spacer(GlanceModifier.width(16.dp))
            //workaround for aligning text in center by vertical
            Column(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
                    .fillMaxSize()
                    .defaultWeight()
            ) {
                Text(
                    "TodoText",//stringResource(coffeeType.nameId),
                    style = TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = ColorProvider(Color.White),
                    ),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                )
            }
            /*Row(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)) {
                Spacer(Modifier.width(16.dp))
                val textButtonModifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterVertically)
                    .sizeIn(
                        maxWidth = 32.dp,
                        maxHeight = 32.dp,
                        minWidth = 0.dp,
                        minHeight = 0.dp
                    )
                val isReduceCountAllowed = count > 0
                TextButton(
                    enabled = isReduceCountAllowed,
                    onClick = {
                        coffeeListViewModel.newIntent(
                            DaysCoffeesIntent.MinusCoffee(
                                localDate,
                                coffeeType
                            )
                        )
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = textButtonModifier
                ) {
                    androidx.compose.material.Text("-")
                }
                androidx.compose.material.Text(
                    "$count", style = typography.body2,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                )
                TextButton(
                    onClick = {
                        coffeeListViewModel.newIntent(
                            DaysCoffeesIntent.PlusCoffee(
                                localDate,
                                coffeeType
                            )
                        )
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = textButtonModifier
                ) {
                    androidx.compose.material.Text("+")
                }
            }*/
        }
    }

    @Composable
    fun BigWidget() {
        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
            items(listOf(Unit, Unit, Unit)) {
                HorizontalWidget(
                    modifier = GlanceModifier.padding(24.dp).fillMaxWidth().height(100.dp)
                )
                /*Row {
                    Text("Horizontal")
                    Text("Horizontal")
                }*/
            }
        }
    }

}
