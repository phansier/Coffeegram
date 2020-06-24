package ru.beryukhov.coffeegram

import android.content.Context
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredSize
import androidx.ui.material.Divider
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Call
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import ru.beryukhov.coffeegram.ui.CoffeegramTheme
import java.text.DateFormatSymbols
import java.util.*

data class DayItem(
    val day: String,
    val icon: VectorAsset? = null
)

@Composable
fun DayCell(dayItem: DayItem, modifier: Modifier = Modifier) {
    Column(horizontalGravity = Alignment.CenterHorizontally, modifier = modifier) {
        with(dayItem) {
            Icon(
                icon ?: Icons.Default.Call,
                tint = if (icon != null) contentColor() else Color.Transparent,
                modifier = Modifier.preferredSize(32.dp)
            )
            Text(
                AnnotatedString(
                    text = day,
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )
        }
    }
}

@Composable
fun WeekRow(dayItems: List<DayItem?>) {
    val weekDaysItems = dayItems.toMutableList()
    weekDaysItems.addAll(listOf(DayItem("")) * (7 - weekDaysItems.size))
    Column(horizontalGravity = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayItem in weekDaysItems) {
                DayCell(dayItem = dayItem ?: DayItem(""), modifier = Modifier.weight(1f))
            }
        }
        Divider()
    }

}

@Composable
fun MonthTableAdjusted(weekItems: List<List<DayItem?>>, modifier: Modifier = Modifier) {
    Column(horizontalGravity = Alignment.CenterHorizontally, modifier = modifier) {
        weekItems.map { WeekRow(dayItems = it) }
    }
}

@Preview(showBackground = true)
@Composable
fun TablePreview() {
    CoffeegramTheme {
        /*Row {
            DayCell(DayItem("Пн"))
            DayCell(DayItem("1"))
            DayCell(DayItem("2", Icons.Default.Call))
        }*/
        /*WeekRow(
            listOf(
                null,
                DayItem("Пн"),
                DayItem("1"),
                DayItem("2", Icons.Default.Call)
            )
        )*/
        getSampleTable()
        //Text(getWeekDaysNames(ContextAmbient.current).toString())
    }
}

@Composable
fun getSampleTable(modifier: Modifier = Modifier) = MonthTableAdjusted(
    listOf(
        getWeekDaysNames(ContextAmbient.current).map { DayItem(it) },
        listOf(
            null,
            DayItem("1"),
            DayItem("2", Icons.Default.Call),
            DayItem("3"), DayItem("4"), DayItem("5"), DayItem("6")
        ),
        getEmptyWeek(7, 13),
        getEmptyWeek(14, 20),
        getEmptyWeek(21, 27),
        getEmptyWeek(28, 30)
    ),
    modifier = modifier
)


fun getWeekDaysNames(context: Context): List<String> =
    getWeekDaysNames(context.resources.configuration.locale)

fun getWeekDaysNames(locale: Locale): List<String> {
    val list = DateFormatSymbols(locale).shortWeekdays.toMutableList()
    // this fun adds empty string at the beginning
    list.removeAt(0)
    return list
}

fun getEmptyWeek(start: Int, end: Int): List<DayItem> {
    val list = mutableListOf<DayItem>()
    for (i in start until end + 1) {
        list.add(DayItem("$i"))
    }
    return list
}