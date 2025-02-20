package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveHorizontalDivider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.Cappuccino
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.times
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.dateFormatSymbolsShortWeekdays
import ru.beryukhov.date_time_utils.getShortDisplayName

data class DayItem(
    val day: String,
    val coffeeType: CoffeeType? = null,
    val dayOfMonth: Int? = null
)

@Composable
fun DayCell(
    dayItem: DayItem,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier,
    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (dayItem.dayOfMonth == null) {
            modifier
        } else {
            modifier.clickable(onClick = {
                navigationStore.newIntent(
                    NavigationIntent.OpenCoffeeListPage(
                        dayItem.dayOfMonth
                    )
                )
            })
        }
    ) {
        with(dayItem) {
            if (coffeeType != null) {
                Image(
                    coffeeType = coffeeType,
                    modifier = Modifier
                        .size(32.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color(0x00000000), // Color.Transparent,
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
fun WeekRow(dayItems: PersistentList<DayItem?>, navigationStore: NavigationStore, modifier: Modifier = Modifier) {
    val weekDaysItems = dayItems.toMutableList()
    weekDaysItems.addAll(listOf(DayItem("")) * (7 - weekDaysItems.size))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayItem in weekDaysItems) {
                DayCell(
                    dayItem = dayItem
                        ?: DayItem(""),
                    navigationStore = navigationStore,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        AdaptiveHorizontalDivider()
    }
}

@Composable
fun MonthTableAdjusted(
    weekItems: PersistentList<PersistentList<DayItem?>>,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        weekItems.map { WeekRow(dayItems = it, navigationStore = navigationStore) }
    }
}

class WeekDayVectorPair(
    val day: Int,
    val weekDay: DayOfWeek,
    var coffeeType: CoffeeType? = null,
) {
    fun toDayItem(): DayItem =
        DayItem("$day", coffeeType, day)
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    filledDayItemsMap: PersistentMap<Int, CoffeeType?>,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier
) {
    val weekDays: PersistentList<DayItem> = getWeekDaysNames().map { DayItem(it) }.toPersistentList()
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
    filledDayItemsMap.forEach { days[it.key]?.coffeeType = it.value }
    val weekDaysStrings = getWeekDaysNames()
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
        weekItems.toPersistentList(),
        navigationStore,
        modifier = modifier
    )
}

@Preview
@Composable
private fun TablePreview() {
    CoffeegramTheme {
        SampleTable()
    }
}

@Composable
fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        YearMonth(2020, Month.JULY),
        mapOf(2 to Cappuccino).toPersistentMap(),
        modifier = modifier,
        navigationStore = NavigationStore()
    )

fun getWeekDaysNames(): List<String> =
    dateFormatSymbolsShortWeekdays()

fun getEmptyWeek(start: Int, end: Int): List<DayItem> {
    val list = mutableListOf<DayItem>()
    for (i in start until end + 1) {
        list.add(DayItem("$i"))
    }
    return list
}
