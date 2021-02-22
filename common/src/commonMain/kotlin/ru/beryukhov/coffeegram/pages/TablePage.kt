package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.date_time.local_date.LocalDate
import ru.beryukhov.coffeegram.date_time.local_date.dayOfMonth
import ru.beryukhov.coffeegram.date_time.local_date.monthValue
import ru.beryukhov.coffeegram.date_time.local_date.year
import ru.beryukhov.coffeegram.date_time.year_month.YearMonth
import ru.beryukhov.coffeegram.date_time.year_month.getFullMonthName
import ru.beryukhov.coffeegram.date_time.year_month.monthValue
import ru.beryukhov.coffeegram.date_time.year_month.year
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.MonthTable


@Composable
fun TableAppBar(
    yearMonth: YearMonth,
    navigationStore: NavigationStore
) {
    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = yearMonth.getFullMonthName(Locale.current),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )

        }
    },
        navigationIcon = {
            IconButton(
                onClick = { navigationStore.newIntent(NavigationIntent.PreviousMonth) },
                modifier = Modifier.semantics {
                    contentDescription = "ArrowLeft"
                }) { Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "") }
        },
        actions = {
            IconButton(
                onClick = { navigationStore.newIntent(NavigationIntent.NextMonth) },
                modifier = Modifier.semantics {
                    testTag = "ArrowRight"
                }) { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "") }
        }
    )
}

//@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TablePage(
    yearMonth: YearMonth,
    daysCoffeesStore: DaysCoffeesStore,
    navigationStore: NavigationStore
) {
    val coffeesState by daysCoffeesStore.state.collectAsState()

    Column(horizontalAlignment = Alignment.End) {
        MonthTable(
            yearMonth,
            coffeesState.coffees.filter { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.year == yearMonth.year && entry.key.monthValue == yearMonth.monthValue }
                .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
                .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getCoffeeType() },
            navigationStore,
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}