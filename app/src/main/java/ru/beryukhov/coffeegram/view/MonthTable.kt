package ru.beryukhov.coffeegram.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.date_time_utils.YearMonth
import java.text.DateFormatSymbols
import java.util.Locale
import ru.beryukhov.coffeegram.common.R as common_R

private data class DayItem(
    val day: String,
    val isToday: Boolean = false,
    @DrawableRes val iconId: Int? = null,
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
            HorizontalDivider()
        }
    }
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    today: LocalDate,
    filledDayItemsMap: PersistentMap<Int, Int?>,
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
                        text = getWeekDaysNames(LocalContext.current)[index],
                        textAlign = TextAlign.Center,
                    )
                    HorizontalDivider()
                }
            }
        }
        val itemsOffset = yearMonth.atDay(1).dayOfWeek.ordinal + 1
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
                        iconId = filledDayItemsMap[day + 1],
                        dayOfMonth = day + 1
                    )
                    DayCell(dayItem = dayItem, onClick = { onClick(day + 1) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TablePreview() {
    CoffeegramTheme {
        SampleTable()
    }
}

@Composable
internal fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        yearMonth = YearMonth(2020, Month(7)),
        today = LocalDate(2020, 7, 14),
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
