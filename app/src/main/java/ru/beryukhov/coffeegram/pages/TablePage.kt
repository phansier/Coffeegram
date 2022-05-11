package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.view.MonthTable

@Composable
fun TableAppBar(
    yearMonth: YearMonth,
    tablePageViewModel: TablePageViewModel = getViewModel<TablePageViewModelImpl>()
) {
    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = yearMonth.month.getDisplayName(
                        TextStyle.FULL,
                        LocalContext.current.resources.configuration.locale
                    ),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )
        }
    },
        navigationIcon = {
            IconButton(
                onClick = { tablePageViewModel.newIntent(NavigationIntent.PreviousMonth) },
                modifier = Modifier.semantics {
                    contentDescription = "ArrowLeft"
                }) { Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "") }
        },
        actions = {
            IconButton(
                onClick = { tablePageViewModel.newIntent(NavigationIntent.NextMonth) },
                modifier = Modifier.testTag("ArrowRight")
            ) { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "") }
        }
    )
}

@Composable
fun ColumnScope.TablePage(
    yearMonth: YearMonth,
    tablePageViewModel: TablePageViewModel = getViewModel<TablePageViewModelImpl>()
) {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
        MonthTable(
            yearMonth = yearMonth,
            filledDayItemsMap = tablePageViewModel.getFilledDayItemsMap(yearMonth),
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    Column {
        val date = localDateStub
        TablePage(
            yearMonth = YearMonth.of(date.year, date.month),
            tablePageViewModel = TablePageViewModelStub
        )
    }
}
