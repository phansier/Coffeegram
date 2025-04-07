package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.dateFormatSymbolsShortWeekdays

private data class DayItem(
    val day: String,
    val isToday: Boolean = false,
    val coffeePicture: Picture = Picture.EMPTY,
    val dayOfMonth: Int? = null
)

@Composable
private fun DayCell(
    dayItem: DayItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                enabled = onClick != null,
                onClick = onClick ?: {}
            )
            .background(if (dayItem.isToday) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .testTag("Day")
    ) {
        with(dayItem) {
            coffeePicture(
                modifier = Modifier
                    .size(32.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                AnnotatedString(
                    text = day,
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    today: LocalDate,
    filledDayItemsMap: PersistentMap<Int, Picture>,
    onClick: (dayOfMonth: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(7) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = getWeekDaysNames()[index],
                        textAlign = TextAlign.Center,
                    )
                    HorizontalDivider()
                }
            }
        }
        val itemsOffset = yearMonth.atDay(1).dayOfWeek.ordinal
        val daysInMonth = yearMonth.lengthOfMonth()
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(itemsOffset + daysInMonth) { index ->
                val day = index - itemsOffset
                if (day >= 0) {
                    val dayItem = DayItem(
                        day = (day + 1).toString(),
                        isToday = yearMonth.atDay(day + 1) == today,
                        coffeePicture = filledDayItemsMap[day + 1] ?: Picture.EMPTY,
                        dayOfMonth = day + 1
                    )
                    DayCell(dayItem = dayItem, onClick = { onClick(day + 1) })
                }
            }
        }
    }
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
        yearMonth = YearMonth(2020, Month.JULY),
        today = LocalDate(2020, 7, 14), // tuesday
        filledDayItemsMap = mapOf(2 to CoffeeTypes.Cappuccino.icon).toPersistentMap(),
        modifier = modifier,
        onClick = {},
    )

fun getWeekDaysNames(): List<String> =
    dateFormatSymbolsShortWeekdays()
