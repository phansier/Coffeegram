package ru.beryukhov.coffeegram.pages

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope.weight
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Call
import androidx.ui.material.icons.filled.KeyboardArrowLeft
import androidx.ui.material.icons.filled.KeyboardArrowRight
import androidx.ui.text.AnnotatedString
import androidx.ui.text.ParagraphStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.view.MonthTable

@Composable
fun TablePage(yearMonthFlow: MutableStateFlow<YearMonth>, filledDaysMapFlow: MutableStateFlow<Map<LocalDate, DayCoffee>> = MutableStateFlow(mapOf())) {
    val yearMonth by yearMonthFlow.collectAsState()

    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = yearMonth.month.getDisplayName(TextStyle.FULL, ContextAmbient.current.resources.configuration.locale),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )

        }
    },
        navigationIcon = { IconButton(onClick = {yearMonthFlow.value = yearMonthFlow.value.minusMonths(1)}) { Icon(Icons.Default.KeyboardArrowLeft) } },
        actions = { IconButton(onClick = {yearMonthFlow.value = yearMonthFlow.value.plusMonths(1)}) { Icon(Icons.Default.KeyboardArrowRight) } }
    )

    Column(modifier = Modifier.weight(1f), horizontalGravity = Alignment.End) {
        MonthTable(
            yearMonth,
            mapOf(2 to Icons.Default.Call),//todo replace by real data
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}