@file:Suppress("ModifierMissing")

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.chart_title_distribution
import coffeegram.cmp_common.generated.resources.chart_title_over_time
import coffeegram.cmp_common.generated.resources.chart_title_weekly
import coffeegram.cmp_common.generated.resources.no_data_available
import coffeegram.cmp_common.generated.resources.tab_all_time
import coffeegram.cmp_common.generated.resources.tab_weekly
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.ProvideVicoTheme
import com.patrykandpatrick.vico.multiplatform.m3.common.rememberM3VicoTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.components.dayNames
import ru.beryukhov.coffeegram.components.getShortMonthName
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.date_time_utils.YearMonth
import kotlin.time.Clock

@Composable
fun CoffeeCharts(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    val windowLayout = LocalWindowLayout.current
    ProvideVicoTheme(rememberM3VicoTheme()) {
        if (windowLayout.isExpandedWidth) {
            ChartsOverview(coffeeState, modifier)
        } else {
            TabbedCharts(coffeeState, allTimeSideBySide = windowLayout.isWide, modifier = modifier)
        }
    }
}

@Composable
private fun TabbedCharts(
    coffeeState: DaysCoffeesState,
    allTimeSideBySide: Boolean,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(Res.string.tab_weekly), stringResource(Res.string.tab_all_time))

    Column(modifier = modifier.fillMaxWidth()) {
        SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTabIndex) {
            0 -> WeeklyChartSection(
                coffeeState = coffeeState,
                modifier = Modifier.padding(16.dp).chartWidth().align(Alignment.CenterHorizontally),
            )
            1 -> AllTimeCharts(coffeeState, sideBySide = allTimeSideBySide)
        }
    }
}

@Composable
private fun ChartsOverview(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    val hasData = coffeeState.coffees.isNotEmpty()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScrollHidingTopBar()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = MaxChartWidth * 2).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ChartTitle(stringResource(Res.string.chart_title_weekly), Modifier.weight(1f))
                ChartTitle(stringResource(Res.string.chart_title_over_time), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                WeeklyChart(coffeeState, Modifier.weight(1f))
                if (hasData) {
                    OverTimeChart(coffeeState, Modifier.weight(1f))
                } else {
                    NoChartData(Modifier.weight(1f))
                }
            }
        }
        if (hasData) {
            DistributionChartSection(coffeeState, Modifier.chartWidth())
        }
    }
}

@Composable
private fun AllTimeCharts(coffeeState: DaysCoffeesState, sideBySide: Boolean) {
    if (coffeeState.coffees.isEmpty()) {
        NoChartData()
        return
    }
    if (sideBySide) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OverTimeChartSection(coffeeState, Modifier.weight(1f).verticalScrollHidingTopBar())
            DistributionChartSection(coffeeState, Modifier.weight(1f).verticalScrollHidingTopBar())
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScrollHidingTopBar(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OverTimeChartSection(coffeeState, Modifier.chartWidth())
            DistributionChartSection(coffeeState, Modifier.chartWidth())
        }
    }
}

private val MaxChartWidth = 720.dp

private fun Modifier.chartWidth(): Modifier = widthIn(max = MaxChartWidth).fillMaxWidth()

@Composable
private fun NoChartData(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(stringResource(Res.string.no_data_available))
    }
}

@Composable
private fun ChartSection(
    title: String,
    modifier: Modifier = Modifier,
    chart: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        ChartTitle(title)
        Spacer(modifier = Modifier.height(16.dp))
        chart()
    }
}

@Composable
private fun ChartTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun WeeklyChartSection(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    ChartSection(title = stringResource(Res.string.chart_title_weekly), modifier = modifier) {
        WeeklyChart(coffeeState)
    }
}

@Composable
private fun WeeklyChart(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))
    val weekData = weeklyChartData(startOfWeek, coffeeState)
    val entries = entries(weekData)
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(x = entries.first, y = entries.second) }
        }
    }
    val dayNames = dayNames
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = { _, value, _ ->
                    dayNames.getOrNull(weekData.getOrNull(value.toInt())?.dayIndex ?: -1) ?: ""
                }
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
private fun OverTimeChartSection(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    ChartSection(title = stringResource(Res.string.chart_title_over_time), modifier = modifier) {
        OverTimeChart(coffeeState)
    }
}

@Composable
private fun OverTimeChart(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    val aggregatedData = remember(coffeeState) { aggregation(coffeeState) }
    LineChart(aggregatedData.display(), modifier)
}

@Composable
private fun DistributionChartSection(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    ChartSection(title = stringResource(Res.string.chart_title_distribution), modifier = modifier) {
        ColumnChart(coffeeState)
    }
}

private const val MONTHLY_AGGREGATION_THRESHOLD_DAYS = 31

private fun aggregation(coffeeState: DaysCoffeesState): List<AggregatedRawData> {
    val dates = coffeeState.coffees.keys
    val daysBetween = (dates.max() - dates.min()).days
    return if (daysBetween > MONTHLY_AGGREGATION_THRESHOLD_DAYS) {
        monthlyAggregation(coffeeState)
    } else {
        dailyAggregation(coffeeState)
    }
}

internal fun weeklyChartData(
    startOfWeek: LocalDate,
    coffeeState: DaysCoffeesState
) = (0..6).map { dayOffset ->
    val date = startOfWeek.plus(DatePeriod(days = dayOffset))
    val dayCoffee = coffeeState.coffees[date] ?: DayCoffee()
    val totalForDay = dayCoffee.coffeeCountMap.values.sum()

    WeeklyChartData(
        date = date,
        dayIndex = date.dayOfWeek.ordinal,
        totalCoffees = totalForDay,
    )
}

internal fun entries(weekData: List<WeeklyChartData>): Pair<List<Int>, List<Int>> =
    weekData.mapIndexed { index, data ->
        index to data.totalCoffees
    }.unzip()

@Composable
private fun List<AggregatedRawData>.display(): ImmutableList<AggregatedData> =
    this.map {
        when (it) {
            is AggregatedDailyData -> AggregatedData(
                "${getShortMonthName(it.month)} ${it.day}",
                totalCount = it.totalCount,
            )

            is AggregatedMonthlyData -> AggregatedData(
                label = "${getShortMonthName(it.month)} ${it.year}",
                totalCount = it.totalCount,
            )
        }
    }.toImmutableList()

@Composable
private fun ColumnChart(coffeeState: DaysCoffeesState) {
    // Create coffee type distribution data
    val typeDistribution = CoffeeTypes.entries.map { coffeeType ->
        val total = coffeeState.coffees.values.sumOf {
            it.coffeeCountMap[coffeeType] ?: 0
        }

        CoffeeTypeCount(
            type = coffeeType,
            count = total
        )
    }.sortedByDescending { it.count }
    val typeChartEntries = typeDistribution.mapIndexed { index, data ->
        index to data.count
    }.unzip()

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(x = typeChartEntries.first, y = typeChartEntries.second) }
        }
    }

    val names = typeDistribution.map { printableText(it.type.localizedName) }

    // Column chart for type distribution
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    labelRotationDegrees = 90f,
                    valueFormatter = { _, value, _ ->
                        names.getOrNull(value.toInt()) ?: ""
                    }
                ),
            ),
        modelProducer = modelProducer,
        modifier = Modifier,
    )
}

@Composable
private fun LineChart(aggregatedData: ImmutableList<AggregatedData>, modifier: Modifier = Modifier) {
    // Create chart entries
    val chartEntries = aggregatedData.mapIndexed { index: Int, data: AggregatedData ->
        index to data.totalCount
    }.unzip()
    val chartEntryModelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        chartEntryModelProducer.runTransaction {
            lineSeries { series(x = chartEntries.first, y = chartEntries.second) }
        }
    }
    // Line chart with Vico
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        aggregatedData.getOrNull(value.toInt())?.label ?: ""
                    }
                ),
            ),
        modelProducer = chartEntryModelProducer,
        modifier = modifier,
    )
}

internal fun dailyAggregation(coffeeState: DaysCoffeesState): List<AggregatedDailyData> =
    coffeeState.coffees.map { (date, dayCoffee) ->
        AggregatedDailyData(
            month = date.month,
            day = date.day,
            totalCount = dayCoffee.coffeeCountMap.values.sum(),
        ) to date
    }.sortedBy { it.second }.map { it.first }

internal fun monthlyAggregation(coffeeState: DaysCoffeesState): List<AggregatedMonthlyData> =
    coffeeState.coffees.entries.groupBy { entry ->
        YearMonth(entry.key.year, entry.key.month)
    }.map { (yearMonth, entries) ->
        val typeCounts = mutableMapOf<CoffeeType, Int>()
        entries.forEach { entry ->
            entry.value.coffeeCountMap.forEach { (type, count) ->
                typeCounts[type] = (typeCounts[type] ?: 0) + count
            }
        }

        AggregatedMonthlyData(
            year = yearMonth.year,
            month = yearMonth.month,
            // label = "${getShortMonthName(yearMonth.month)} ${yearMonth.year}",
            totalCount = typeCounts.values.sum(),
        ) to yearMonth
    }.sortedBy { it.second }.map { it.first }

data class WeeklyChartData(
    val date: LocalDate,
    val dayIndex: Int,
    val totalCoffees: Int,
)

data class AggregatedData(
    val label: String,
    val totalCount: Int,
)

sealed interface AggregatedRawData {
    val totalCount: Int
}

data class AggregatedDailyData(
    val month: Month,
    val day: Int,
    override val totalCount: Int,
) : AggregatedRawData

data class AggregatedMonthlyData(
    val year: Int,
    val month: Month,
    override val totalCount: Int,
) : AggregatedRawData

data class CoffeeTypeCount(val type: CoffeeType, val count: Int)
