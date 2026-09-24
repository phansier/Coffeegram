@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.adaptive.icons.AdaptiveIcons
import com.slapps.cupertino.adaptive.icons.KeyboardArrowLeft
import com.slapps.cupertino.adaptive.icons.KeyboardArrowRight
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.beryukhov.coffeegram.app_ui.LocalPhoneFrameInsets
import ru.beryukhov.coffeegram.app_ui.handleKeyDown
import ru.beryukhov.coffeegram.components.MonthTableComponent
import ru.beryukhov.coffeegram.components.getFullMonthName
import ru.beryukhov.coffeegram.view.MonthTable
import kotlin.time.Clock

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun MonthTableScreen(
    component: MonthTableComponent,
    modifier: Modifier = Modifier,
    selectedDay: LocalDate? = null,
) {
    val monthTableScreenState by component.models.collectAsState()

    MonthTable(
        yearMonth = monthTableScreenState.yearMonth,
        today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        filledDayItemsMap = monthTableScreenState.filledDayItemsMap,
        onClick = { dayOfMonth: Int ->
            component.onDayClick(dayOfMonth)
        },
        modifier = modifier.onKeyEvent { event ->
            event.handleKeyDown(Key.PageUp, action = component::onDecrementMonth) ||
                event.handleKeyDown(Key.PageDown, action = component::onIncrementMonth)
        },
        selectedDay = selectedDay,
        compact = LocalWindowLayout.current.isCompactHeight,
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun MonthTableAppBar(
    component: MonthTableComponent,
    modifier: Modifier = Modifier,
    selectedDay: LocalDate? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val screenState by component.models.collectAsState()

    AdaptiveTopAppBar(
        modifier = modifier,
        title = {
            val monthLabel = getFullMonthName(screenState.yearMonth.month)
            val titleText = if (selectedDay != null && selectedDay.month == screenState.yearMonth.month) {
                "$monthLabel ${selectedDay.day}"
            } else {
                monthLabel
            }
            TopBarTitle(
                title = titleText,
                eyebrow = "${screenState.yearMonth.year}",
                cupertinoTitle = "$titleText ${screenState.yearMonth.year}",
                modifier = Modifier.testTag("Month"),
            )
        },
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TopBarIconButton(
                    onClick = component::onDecrementMonth,
                    modifier = Modifier.semantics {
                        contentDescription = "ArrowLeft"
                    }
                ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowLeft, contentDescription = "") }
                TopBarIconButton(
                    onClick = component::onIncrementMonth,
                    modifier = Modifier.semantics {
                        testTag = "ArrowRight"
                    }
                ) { Icon(imageVector = AdaptiveIcons.Outlined.KeyboardArrowRight, contentDescription = "") }
            }
        },
        windowInsets = TopAppBarDefaults.windowInsets.union(LocalPhoneFrameInsets.current),
        adaptation = { material { applyTopBarScrollBehavior(scrollBehavior) } },
    )
}
