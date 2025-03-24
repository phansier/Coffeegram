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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.adaptive.icons.AdaptiveIcons
import com.slapps.cupertino.adaptive.icons.KeyboardArrowLeft
import com.slapps.cupertino.adaptive.icons.KeyboardArrowRight
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.calculate
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.getFullMonthName

@OptIn(ExperimentalAdaptiveApi::class)
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
    daysCoffeesStore: DaysCoffeesStore,
    navigationStore: NavigationStore,
    modifier: Modifier = Modifier,
) {
    val coffeesState by daysCoffeesStore.state.collectAsState()

    Column(horizontalAlignment = Alignment.End, modifier = modifier.weight(1f)) {
        val navState by navigationStore.state.collectAsState()
        val yearMonth by remember(navState) { derivedStateOf { (navState as NavigationState.TablePage).yearMonth } }
        MonthTable(
            yearMonth = yearMonth,
            filledDayItemsMap = coffeesState.calculate(yearMonth),
            onClick = { dayOfMonth: Int ->
                navigationStore.newIntent(
                    NavigationIntent.OpenCoffeeListPage(
                        dayOfMonth
                    )
                )
            },
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}
