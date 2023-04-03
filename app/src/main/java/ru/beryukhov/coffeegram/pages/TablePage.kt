package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.collections.immutable.toPersistentMap
import org.koin.androidx.compose.getViewModel
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.view.MonthTable

@ExperimentalMaterial3Api
@Composable
fun TableAppBar(
    yearMonth: YearMonth,
    currentPage: Int,
    modifier: Modifier = Modifier,
    tablePageViewModel: TablePageViewModel = getViewModel<TablePageViewModelImpl>()
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = AnnotatedString(
                        text = yearMonth.minusMonths(currentPage.toLong()).month.getDisplayName(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.TablePage(
    yearMonth: YearMonth,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tablePageViewModel: TablePageViewModel = getViewModel<TablePageViewModelImpl>()
) {
    Column(horizontalAlignment = Alignment.End, modifier = modifier.weight(1f)) {

        HorizontalPager(
            pageCount = 12,
            reverseLayout = true,
            pageSpacing = 16.dp,
            key = { page -> onPageChange(page).let { page } }
        ) { page ->
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = yearMonth.minusMonths(page.toLong()).month.getDisplayName(
                        TextStyle.FULL,
                        LocalContext.current.resources.configuration.locale
                    ),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )
            MonthTable(
                yearMonth = yearMonth.minusMonths(page.toLong()),
                filledDayItemsMap = tablePageViewModel.getFilledDayItemsMap(yearMonth.minusMonths(page.toLong()))
                    .toPersistentMap(),
                onClick = { dayOfMonth: Int ->
                    tablePageViewModel.newIntent(
                        NavigationIntent.OpenCoffeeListPage(
                            dayOfMonth
                        )
                    )
                },
                modifier = Modifier.wrapContentHeight()
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            LottieCoffee(modifier = Modifier.weight(1f, fill = false), alignment = Alignment.BottomStart)
            Text("${yearMonth.year}", modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Bottom))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column {
        val date = localDateStub
        TablePage(
            yearMonth = YearMonth.of(date.year, date.month),
            onPageChange = { },
            tablePageViewModel = TablePageViewModelStub
        )
    }
}

@Composable
fun LottieCoffee(modifier: Modifier = Modifier, alignment: Alignment = Alignment.Center,) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_coffee))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        alignment = alignment,
    )
}
