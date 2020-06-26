package ru.beryukhov.coffeegram.view

import android.content.Context
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
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
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.times
import java.text.DateFormatSymbols
import java.util.*

data class DayItem(
    val day: String,
    val icon: VectorAsset? = null,
    val dayOfMonth: Int? = null
)

@Composable
fun DayCell(
    dayItem: DayItem,
    modifier: Modifier = Modifier,
    dateFlow: MutableStateFlow<Int> = MutableStateFlow(0)
) {
    Column(horizontalGravity = Alignment.CenterHorizontally, modifier =
    if (dayItem.dayOfMonth == null) modifier else
        modifier.clickable(onClick = { dateFlow.value = dayItem.dayOfMonth })
    ) {
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
fun WeekRow(dayItems: List<DayItem?>, dateFlow: MutableStateFlow<Int> = MutableStateFlow(0)) {
    val weekDaysItems = dayItems.toMutableList()
    weekDaysItems.addAll(listOf(DayItem("")) * (7 - weekDaysItems.size))
    Column(horizontalGravity = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayItem in weekDaysItems) {
                DayCell(
                    dayItem = dayItem
                        ?: DayItem(""),
                    dateFlow = dateFlow,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Divider()
    }

}

@Composable
fun MonthTableAdjusted(
    weekItems: List<List<DayItem?>>,
    dateFlow: MutableStateFlow<Int> = MutableStateFlow(0),
    modifier: Modifier = Modifier
) {
    Column(horizontalGravity = Alignment.CenterHorizontally, modifier = modifier) {
        weekItems.map { WeekRow(dayItems = it, dateFlow = dateFlow) }
    }
}

data class WeekDayVectorPair(
    val day: Int,
    val weekDay: DayOfWeek,
    var vector: VectorAsset? = null
) {
    fun toDayItem(): DayItem =
        DayItem("$day", vector, day)
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    filledDayItemsMap: Map<Int, VectorAsset?>,
    dateFlow: MutableStateFlow<Int> = MutableStateFlow(0),
    modifier: Modifier = Modifier
) {
    val weekDays: List<DayItem> = getWeekDaysNames(
        ContextAmbient.current
    ).map { DayItem(it) }
    val days1to31 = mutableListOf<Int>()
    for (i in 1 until 31) {
        days1to31.add(i)
    }
    val days = days1to31.filter { yearMonth.isValidDay(it) }
        .associateBy<Int, Int, WeekDayVectorPair>(
            { it },
            {
                WeekDayVectorPair(
                    it,
                    yearMonth.atDay(it).dayOfWeek
                )
            })
        .toMutableMap()
    filledDayItemsMap.forEach { days[it.key]?.vector = it.value }
    val weekDaysStrings =
        getWeekDaysNames(ContextAmbient.current)
    val numberOfFirstDay = weekDaysStrings.indexOf(
        days[1]!!.weekDay.getDisplayName(
            TextStyle.SHORT,
            ContextAmbient.current.resources.configuration.locale
        )
    )
    val daysList: List<WeekDayVectorPair> = days.toList().sortedBy { it.first }.map { it.second }
    val firstWeek: List<DayItem> =
        listOf(DayItem("")) * (numberOfFirstDay) + daysList.take(7 - numberOfFirstDay)
            .map(WeekDayVectorPair::toDayItem)
    val secondToSixWeeks: List<List<DayItem>> = listOf(2, 3, 4, 5, 6).map {
        daysList.drop(7 * (it - 1) - numberOfFirstDay).take(7)
    }.filterNot { it.isEmpty() }
        .map { it.map(WeekDayVectorPair::toDayItem) }

    val weekItems = mutableListOf(
        weekDays,
        firstWeek
    )
    weekItems.addAll(secondToSixWeeks)
    return MonthTableAdjusted(
        weekItems,
        dateFlow,
        modifier = modifier
    )
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
        SampleTable()
        //Text(getWeekDaysNames(ContextAmbient.current).toString())
    }
}

@Composable
fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        YearMonth.of(2020, 7),
        mapOf(2 to Icons.Default.Call),
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