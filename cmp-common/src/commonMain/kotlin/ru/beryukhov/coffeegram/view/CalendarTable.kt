package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.components.dayNames
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.date_time_utils.YearMonth

private data class DayItem(
    val day: String,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val coffeePicture: Picture = Picture.EMPTY,
    val dayOfMonth: Int? = null
)

private val CompactDayIconSize = 20.dp
private val MinDayIconSize = 24.dp
private val MaxDayIconSize = 64.dp
private val DayLabelHeight = 32.dp
private val WeekdayHeaderHeight = 32.dp
private val MinDayRowHeight = 56.dp
private val MinCompactDayRowHeight = 32.dp
private val DayColumnGap = 4.dp
private const val DAYS_IN_WEEK = 7
private const val DAY_ICON_WIDTH_FRACTION = 0.7f

@Composable
private fun DayCell(
    dayItem: DayItem,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = when {
        dayItem.isSelected -> MaterialTheme.colorScheme.secondaryContainer
        dayItem.isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val cellModifier = modifier
        .padding(vertical = 2.dp)
        .clickable(
            enabled = onClick != null,
            onClick = onClick ?: {}
        )
        .background(
            color = backgroundColor,
            shape = RoundedCornerShape(8.dp)
        )
        .testTag("Day")
    if (compact) {
        CompactDayCellContent(dayItem, cellModifier)
    } else {
        DayCellContent(dayItem, iconSize, cellModifier)
    }
}

@Composable
private fun DayCellContent(dayItem: DayItem, iconSize: Dp, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        dayItem.coffeePicture(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(iconSize)
        )
        Text(
            text = AnnotatedString(
                text = dayItem.day,
                paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
            ),
            style = typography.bodyMedium,
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp))
    }
}

@Composable
private fun CompactDayCellContent(dayItem: DayItem, modifier: Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        dayItem.coffeePicture(modifier = Modifier.size(CompactDayIconSize))
        Text(
            text = dayItem.day,
            style = typography.bodySmall,
        )
    }
}

@Composable
private fun WeekdayHeader(name: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 8.dp),
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            style = typography.bodySmall
        )
        HorizontalDivider()
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun MonthTable(
    yearMonth: YearMonth,
    today: LocalDate,
    filledDayItemsMap: PersistentMap<Int, Picture>,
    onClick: (dayOfMonth: Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedDay: LocalDate? = null,
    compact: Boolean = false,
) {
    val firstDayOffset = yearMonth.atDay(1).dayOfWeek.ordinal
    val daysInMonth = yearMonth.lengthOfMonth()
    val weeks = (firstDayOffset + daysInMonth + DAYS_IN_WEEK - 1) / DAYS_IN_WEEK
    val minRowHeight = if (compact) MinCompactDayRowHeight else MinDayRowHeight

    BoxWithConstraints(modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
        val iconSize = dayIconSize(maxWidth, maxHeight, weeks)
        Grid(
            config = {
                repeat(DAYS_IN_WEEK) { column(1.fr) }
                row(GridTrackSize.Auto)
                repeat(weeks) { row(GridTrackSize.MinMax(minRowHeight, 1.fr)) }
                columnGap(DayColumnGap)
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            dayNames.forEach { name -> WeekdayHeader(name, Modifier.fillMaxWidth()) }
            for (dayOfMonth in 1..daysInMonth) {
                val cellIndex = firstDayOffset + dayOfMonth - 1
                val cellDate = yearMonth.atDay(dayOfMonth)
                DayCell(
                    dayItem = DayItem(
                        day = dayOfMonth.toString(),
                        isToday = cellDate == today,
                        isSelected = cellDate == selectedDay,
                        coffeePicture = filledDayItemsMap[dayOfMonth] ?: Picture.EMPTY,
                        dayOfMonth = dayOfMonth
                    ),
                    iconSize = iconSize,
                    compact = compact,
                    onClick = { onClick(dayOfMonth) },
                    modifier = Modifier
                        .gridItem(row = cellIndex / DAYS_IN_WEEK + 2, column = cellIndex % DAYS_IN_WEEK + 1)
                        .fillMaxSize(),
                )
            }
        }
    }
}

private fun dayIconSize(width: Dp, height: Dp, weeks: Int): Dp {
    val cellWidth = (width - DayColumnGap * (DAYS_IN_WEEK - 1)) / DAYS_IN_WEEK
    val cellHeight = if (height.isFinite) (height - WeekdayHeaderHeight) / weeks else MinDayRowHeight
    return minOf(cellWidth * DAY_ICON_WIDTH_FRACTION, cellHeight - DayLabelHeight)
        .coerceIn(MinDayIconSize, MaxDayIconSize)
}

@PreviewLightDark
@Composable
private fun TablePreview() = PreviewTheme {
    SampleTable()
}

@Composable
fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        yearMonth = YearMonth(2020, Month.JULY),
        today = LocalDate(2020, 7, 14), // tuesday
        filledDayItemsMap = mapOf(2 to CoffeeTypes.Cappuccino.icon, 14 to CoffeeTypes.Fredo.icon).toPersistentMap(),
        modifier = modifier,
        onClick = {},
    )
