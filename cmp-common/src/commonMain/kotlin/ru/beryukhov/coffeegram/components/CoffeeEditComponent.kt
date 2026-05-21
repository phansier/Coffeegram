package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.activateDetails
import com.arkivanov.decompose.router.panels.childPanels
import com.arkivanov.decompose.router.panels.childPanelsWebNavigation
import com.arkivanov.decompose.router.panels.dismissDetails
import com.arkivanov.decompose.router.panels.setMode
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.DetailsConfig
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.MainConfig
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.MonthTableScreenStore
import ru.beryukhov.date_time_utils.YearMonth

@OptIn(ExperimentalDecomposeApi::class)
interface CoffeeEditComponent : WebNavigationOwner {
    val panels: Value<ChildPanels<MainConfig, MonthTableComponent, DetailsConfig, DayListComponent, Nothing, Nothing>>

    fun onBack()
    fun setMode(mode: ChildPanelsMode)

    @Serializable
    data object MainConfig

    @Serializable
    sealed interface DetailsConfig {
        @Serializable
        data class DayList(val date: LocalDate) : DetailsConfig
    }
}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalSerializationApi::class)
class DefaultCoffeeEditComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
) : CoffeeEditComponent, ComponentContext by context {
    private val navigation = PanelsNavigation<MainConfig, DetailsConfig, Nothing>()

    private val monthTableScreenStore = MonthTableScreenStore(
        initialStoreState = daysCoffeesStore.state.value,
    )

    override val panels: Value<ChildPanels<
        MainConfig, MonthTableComponent, DetailsConfig, DayListComponent, Nothing, Nothing
        >> =
        childPanels(
            source = navigation,
            serializers = MainConfig.serializer() to DetailsConfig.serializer(),
            initialPanels = { Panels(main = MainConfig) },
            handleBackButton = true,
            mainFactory = { _, ctx ->
                DefaultMonthTableComponent(
                    context = ctx,
                    daysCoffeesStore = daysCoffeesStore,
                    monthTableScreenStore = monthTableScreenStore,
                    onNavigate = { date ->
                        navigation.activateDetails(DetailsConfig.DayList(date))
                    },
                )
            },
            detailsFactory = { config, ctx ->
                when (config) {
                    is DetailsConfig.DayList -> DefaultDayListComponent(
                        context = ctx,
                        daysCoffeesStore = daysCoffeesStore,
                        date = config.date,
                        onBackNavigation = {
                            navigation.dismissDetails()
                        },
                    )
                }
            },
        )

    override val webNavigation: WebNavigation<*> =
        childPanelsWebNavigation(
            navigator = navigation,
            panels = panels,
            serializers = MainConfig.serializer() to DetailsConfig.serializer(),
            pathMapper = { state ->
                when (val details = state.details?.configuration) {
                    is DetailsConfig.DayList -> "day/${details.date}"
                    null -> ""
                }
            },
        )

    init {
        // Keep the details panel in sync with the visible month: when the user navigates
        // months in MonthTable, shift the open day to the same day-of-month in the new
        // month (clamped to its last day).
        monthTableScreenStore.state
            .map { it.yearMonth }
            .distinctUntilChanged()
            .onEach { yearMonth ->
                val currentDate = (panels.value.details?.configuration as? DetailsConfig.DayList)?.date
                    ?: return@onEach
                if (currentDate.year == yearMonth.year && currentDate.month == yearMonth.month) return@onEach
                navigation.activateDetails(
                    DetailsConfig.DayList(yearMonth.clampedDate(currentDate.day))
                )
            }
            .launchIn(CoroutineScope(Dispatchers.Default + SupervisorJob()))
    }

    override fun onBack() {
        navigation.dismissDetails()
    }

    override fun setMode(mode: ChildPanelsMode) {
        navigation.setMode(mode)
    }
}

private fun YearMonth.clampedDate(day: Int): LocalDate =
    atDay(day.coerceIn(1, lengthOfMonth()))
