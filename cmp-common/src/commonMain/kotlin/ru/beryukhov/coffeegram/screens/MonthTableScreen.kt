package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.adaptive.icons.AdaptiveIcons
import com.slapps.cupertino.adaptive.icons.KeyboardArrowLeft
import com.slapps.cupertino.adaptive.icons.KeyboardArrowRight
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.beryukhov.coffeegram.components.MonthTableComponent
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.getFullMonthName

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun MonthTableScreen(
    component: MonthTableComponent,
    modifier: Modifier = Modifier
) {
    val monthTableScreenState by component.models.collectAsState()

    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        MonthTable(
            yearMonth = monthTableScreenState.yearMonth,
            today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            filledDayItemsMap = monthTableScreenState.filledDayItemsMap,
            onClick = { dayOfMonth: Int ->
                component.onDayClick(dayOfMonth)
            },
            modifier = Modifier.weight(1f)
        )
        Text("${monthTableScreenState.yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun MonthTableAppBar(
    component: MonthTableComponent,
    modifier: Modifier = Modifier
) {
    val screenState by component.models.collectAsState()

    AdaptiveTopAppBar(
        modifier = modifier,
        title = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = AnnotatedString(
                        text = getFullMonthName(screenState.yearMonth.month),
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                )
            }
        },
        navigationIcon = {
            AdaptiveIconButton(
                onClick = component::onDecrementMonth,
                modifier = Modifier.semantics {
                    contentDescription = "ArrowLeft"
                }
            ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowLeft, contentDescription = "") }
        },
        actions = {
            AdaptiveIconButton(
                onClick = component::onIncrementMonth,
                modifier = Modifier.semantics {
                    testTag = "ArrowRight"
                }
            ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowRight, contentDescription = "") }
        }
    )
}
