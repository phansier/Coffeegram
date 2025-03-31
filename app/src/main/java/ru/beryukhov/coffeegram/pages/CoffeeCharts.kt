@file:Suppress("ModifierMissing")

package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.date_time_utils.YearMonth

@Composable
fun CoffeeCharts(coffeeState: DaysCoffeesState, modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly", "All Time")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ProvideVicoTheme(rememberM3VicoTheme()) {
            when (selectedTabIndex) {
                0 -> WeeklyCoffeeChart(coffeeState)
                1 -> AllTimeCoffeeChart(coffeeState)
            }
        }
    }
}

@Composable
fun WeeklyCoffeeChart(coffeeState: DaysCoffeesState) {
    // Get dates for the current week (Monday to Sunday)
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val startOfWeek = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))

    // Filter data for current week and aggregate by day
    val weekData = weeklyChartData(startOfWeek, coffeeState)
    val entries = entries(weekData)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Create chart entries
        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                columnSeries { series(x = entries.first, y = entries.second) }
            }
        }
        Text(
            text = "Coffee Consumption This Week",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        weekData.getOrNull(value.toInt())?.dayName ?: ""
                    }
                ),
            ),
            modelProducer = modelProducer,
            modifier = Modifier,
        )
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
        dayName = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
        },
        totalCoffees = totalForDay,
    )
}

internal fun entries(weekData: List<WeeklyChartData>): Pair<List<Int>, List<Int>> =
    weekData.mapIndexed { index, data ->
        index to data.totalCoffees
    }.unzip()

@Composable
fun AllTimeCoffeeChart(coffeeState: DaysCoffeesState) {
    // Only process if we have data
    if (coffeeState.coffees.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("No data available")
        }
        return
    }

    // Get date range for all available data
    val dates = coffeeState.coffees.keys
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val minDate = dates.minOrNull() ?: today
    val maxDate = dates.maxOrNull() ?: today

    // Calculate day difference
    val daysBetween = (maxDate - minDate).days

    // Prepare data
    val useMonthlyAggregation = daysBetween > 31

    // Aggregate data
    val aggregatedData = if (useMonthlyAggregation) {
        monthlyAggregation(coffeeState)
    } else {
        dailyAggregation(coffeeState)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Coffee Consumption Over Time",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
        LineChart(aggregatedData.toImmutableList())

        Spacer(modifier = Modifier.height(24.dp))

        // Coffee type distribution
        Text(
            text = "Coffee Type Distribution",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        ColumnChart(coffeeState)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

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
private fun LineChart(aggregatedData: ImmutableList<AggregatedData>) {
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
        modifier = Modifier,
    )
}

internal fun dailyAggregation(coffeeState: DaysCoffeesState): List<AggregatedData> =
    coffeeState.coffees.map { (date, dayCoffee) ->
        AggregatedData(
            label = "${date.month.name.take(3)} ${date.dayOfMonth}",
            totalCount = dayCoffee.coffeeCountMap.values.sum(),
        ) to date
    }.sortedBy { it.second }.map { it.first }

internal fun monthlyAggregation(coffeeState: DaysCoffeesState): List<AggregatedData> =
    coffeeState.coffees.entries.groupBy { entry ->
        YearMonth(entry.key.year, entry.key.month)
    }.map { (yearMonth, entries) ->
        val typeCounts = mutableMapOf<CoffeeType, Int>()
        entries.forEach { entry ->
            entry.value.coffeeCountMap.forEach { (type, count) ->
                typeCounts[type] = (typeCounts[type] ?: 0) + count
            }
        }

        AggregatedData(
            label = yearMonth.toString(),
            totalCount = typeCounts.values.sum(),
        ) to yearMonth
    }.sortedBy { it.second }.map { it.first }

data class WeeklyChartData(
    val date: LocalDate,
    val dayName: String,
    val totalCoffees: Int,
)

data class AggregatedData(
    val label: String,
    val totalCount: Int,
)

data class CoffeeTypeCount(val type: CoffeeType, val count: Int)
