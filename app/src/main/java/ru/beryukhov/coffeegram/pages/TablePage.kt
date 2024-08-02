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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.getFullMonthName
import ru.beryukhov.date_time_utils.toYearMonth

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun TableAppBar(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val yearMonth = pagerState.currentPage.toYearMonth()

    TopAppBar(
        modifier = modifier,
        title = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.weight(1f).testTag("Month"),
                    text = AnnotatedString(
                        text = getFullMonthName(yearMonth.month),
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "LeftArrow"
                }
            ) { Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "") }
        },
        actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier.testTag("RightArrow")
            ) { Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "") }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.TablePage(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    tablePageViewModel: TablePageViewModel = koinViewModel<TablePageViewModelImpl>(),
) {

    val yearMonth = pagerState.currentPage.toYearMonth()

    LaunchedEffect(key1 = pagerState.currentPage) {
        tablePageViewModel.newIntent(NavigationIntent.SetYearMonth(pagerState.currentPage.toYearMonth()))
    }

    Column(horizontalAlignment = Alignment.End, modifier = modifier.weight(1f).testTag("TableScreen")) {
        HorizontalPager(state = pagerState) {
            MonthTable(
                yearMonth = yearMonth,
                filledDayItemsMap = tablePageViewModel.getFilledDayItemsMap(yearMonth).toPersistentMap(),
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
            Text(
                "${yearMonth.year}",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Bottom)
                    .testTag("Year")
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun Preview() {
    Column {
        val pagerState = rememberPagerState(pageCount = { 36_000 })
        TablePage(
            tablePageViewModel = TablePageViewModelStub,
            pagerState = pagerState
        )
    }
}

@Composable
fun LottieCoffee(modifier: Modifier = Modifier, alignment: Alignment = Alignment.Center) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_coffee))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        alignment = alignment,
    )
}
