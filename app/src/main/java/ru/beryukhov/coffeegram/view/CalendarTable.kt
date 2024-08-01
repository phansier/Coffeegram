package ru.beryukhov.coffeegram.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.times
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.getShortDisplayName
import java.text.DateFormatSymbols
import java.util.Locale
import ru.beryukhov.coffeegram.common.R as common_R

data class DayItem(
    val day: String,
    @DrawableRes val iconId: Int? = null,
    val dayOfMonth: Int? = null
)

@Composable
fun DayCell(
    dayItem: DayItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(
            enabled = onClick != null,
            onClick = onClick ?: {}
        )
    ) {
        with(dayItem) {
            if (iconId != null) {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.Transparent,
                    modifier = Modifier.size(32.dp)
                )
            }

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
fun WeekRow(
    dayItems: PersistentList<DayItem?>,
    onClick: (dayOfMonth: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weekDaysItems = dayItems.toMutableList()
    weekDaysItems.addAll(listOf(DayItem("")) * (7 - weekDaysItems.size))
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayItem in weekDaysItems) {
                DayCell(
                    dayItem = dayItem
                        ?: DayItem(""),
                    onClick = dayItem?.dayOfMonth?.let {
                        { onClick(it) }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun MonthTableAdjusted(
    weekItems: PersistentList<PersistentList<DayItem?>>,
    onClick: (dayOfMonth: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        weekItems.map {
            WeekRow(
                dayItems = it,
                onClick = onClick
            )
        }
    }
}

@Suppress("DataClassShouldBeImmutable")
data class WeekDayVectorPair(
    val day: Int,
    val weekDay: DayOfWeek,
    @DrawableRes var iconId: Int? = null
) {
    fun toDayItem(): DayItem =
        DayItem("$day", iconId, day)
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    filledDayItemsMap: PersistentMap<Int, Int?>,
    onClick: (dayOfMonth: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDays: PersistentList<DayItem> = getWeekDaysNames(
        LocalContext.current
    ).map { DayItem(it) }.toPersistentList()
    val days1to31 = mutableListOf<Int>()
    for (i in 1..31) {
        days1to31.add(i)
    }
    val days = days1to31.filter { yearMonth.isValidDay(it) }
        .associateBy(
            { it },
            {
                WeekDayVectorPair(
                    it,
                    yearMonth.atDay(it).dayOfWeek
                )
            }
        )
        .toMutableMap()
    filledDayItemsMap.forEach { days[it.key]?.iconId = it.value }
    val weekDaysStrings = getWeekDaysNames(LocalContext.current)
    val numberOfFirstDay = weekDaysStrings.indexOf(
        days[1]!!.weekDay.getShortDisplayName()
    )
    val daysList: List<WeekDayVectorPair> = days.toList().sortedBy { it.first }.map { it.second }
    val firstWeek: PersistentList<DayItem> =
        (listOf(DayItem("")) * numberOfFirstDay + daysList.take(7 - numberOfFirstDay)
            .map(WeekDayVectorPair::toDayItem)).toPersistentList()

    val secondToSixWeeks: List<PersistentList<DayItem>> = listOf(2, 3, 4, 5, 6).map {
        daysList.drop(7 * (it - 1) - numberOfFirstDay).take(7)
    }.filterNot { it.isEmpty() }
        .map { it.map(WeekDayVectorPair::toDayItem).toPersistentList() }

    val weekItems = mutableListOf(
        weekDays,
        firstWeek
    )
    weekItems.addAll(secondToSixWeeks)

    return MonthTableAdjusted(
        weekItems = weekItems.toPersistentList(),
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
internal fun TablePreview() {
    CoffeegramTheme {
        SampleTable()
    }
}

@Composable
private fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        yearMonth = YearMonth(2020, Month(7)),
        filledDayItemsMap = mapOf(2 to common_R.drawable.coffee).toPersistentMap(),
        onClick = {},
        modifier = modifier,
    )

fun getWeekDaysNames(context: Context): List<String> =
    getWeekDaysNames(context.resources.configuration.locales[0])

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
