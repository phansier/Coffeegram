package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveIconButton
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.icons.AdaptiveIcons
import com.slapps.cupertino.adaptive.icons.KeyboardArrowLeft
import com.slapps.cupertino.adaptive.icons.KeyboardArrowRight
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.getFullMonthName

@Composable
fun TableAppBar(
    yearMonth: YearMonth,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier,
    ) {
    AdaptiveTopAppBar(
        modifier = modifier,
        title = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = AnnotatedString(
                        text = getFullMonthName(yearMonth.month),
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                )
            }
        },
        navigationIcon = {
            AdaptiveIconButton(
                onClick = { navigationStore.newIntent(NavigationIntent.PreviousMonth) },
                modifier = Modifier.semantics {
                    contentDescription = "ArrowLeft"
                }
            ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowLeft, contentDescription = "") }
        },
        actions = {
            AdaptiveIconButton(
                onClick = { navigationStore.newIntent(NavigationIntent.NextMonth) },
                modifier = Modifier.semantics {
                    testTag = "ArrowRight"
                }
            ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowRight, contentDescription = "") }
        }
    )
}

@Composable
fun ColumnScope.TablePage(
    yearMonth: YearMonth,
    daysCoffeesStore: DaysCoffeesStore,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier,
) {
    val coffeesState by daysCoffeesStore.state.collectAsState()

    Column(horizontalAlignment = Alignment.End, modifier = modifier.weight(1f)) {
        MonthTable(
            yearMonth,
            coffeesState.coffees.filter { entry: Map.Entry<LocalDate, DayCoffee> ->
                entry.key.year == yearMonth.year && entry.key.month == yearMonth.month
            }
                .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
                .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getCoffeeType() }.toPersistentMap(),
            navigationStore,
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}
