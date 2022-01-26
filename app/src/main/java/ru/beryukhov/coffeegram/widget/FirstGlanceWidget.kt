package ru.beryukhov.coffeegram.widget;


import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import ru.beryukhov.coffeegram.MainActivity

class FirstGlanceWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    @Composable
    override fun Content() {
        //val size = LocalSize.current //    java.lang.IllegalStateException: Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied
        //CoffeegramTheme(false) {
            val size = LocalSize.current
            Row(modifier = GlanceModifier.background(color = MaterialTheme.colors.primarySurface)) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colors.background)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "First Glance widget",
                        //modifier = GlanceModifier.fillMaxWidth(),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                    )
                    Button(
                        text = "Do Something",
                        onClick = actionStartActivity<MainActivity>(),
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                if (size.width>100.dp) {
                    Text(
                        text = "Do Something",
//                        onClick = actionStartActivity<MainActivity>(),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                }
            //}

        }
    }
}