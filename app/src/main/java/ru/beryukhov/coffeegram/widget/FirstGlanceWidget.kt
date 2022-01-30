package ru.beryukhov.coffeegram.widget;


import androidx.compose.material.MaterialTheme
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import ru.beryukhov.coffeegram.MainActivity
import ru.beryukhov.coffeegram.R

class FirstGlanceWidget : GlanceAppWidget(errorUiLayout = R.layout.layout_widget_custom_error) {

    companion object {
        //TODO check this sizes after UI implementation
//        private val SMALL_SQUARE = DpSize(120.dp, 120.dp)
//        private val HORIZONTAL_RECTANGLE = DpSize(240.dp, 120.dp)
//        private val BIG_SQUARE = DpSize(240.dp, 240.dp)
//        private val SMALL_SQUARE = DpSize(50.dp, 50.dp)
//        private val HORIZONTAL_RECTANGLE = DpSize(100.dp, 50.dp)
//        private val BIG_SQUARE = DpSize(100.dp, 100.dp)
        private val SMALL_SQUARE = DpSize(180.dp, 110.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(270.dp, 110.dp)
        private val BIG_SQUARE = DpSize(270.dp, 280.dp)
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

        when {
            size.width <= SMALL_SQUARE.width && size.height <= SMALL_SQUARE.height -> SmallWidget()
            size.width <= HORIZONTAL_RECTANGLE.width && size.height <= HORIZONTAL_RECTANGLE.height -> HorizontalWidget()
            else -> BigWidget()
        }

    }

    @Composable
    fun SmallWidget(count: Int = 5) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
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
    fun HorizontalWidget() {
        Row {
            Text("Horizontal")
            Text("Horizontal")
        }
    }

    @Composable
    fun BigWidget() {
        LazyColumn {
            items(listOf(Unit, Unit)) {
                HorizontalWidget()
            }
        }
    }


}
