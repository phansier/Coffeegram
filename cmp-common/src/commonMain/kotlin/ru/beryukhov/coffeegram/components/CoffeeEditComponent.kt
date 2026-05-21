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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.DetailsConfig
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.MainConfig
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

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

    override val panels: Value<ChildPanels<MainConfig, MonthTableComponent, DetailsConfig, DayListComponent, Nothing, Nothing>> =
        childPanels(
            source = navigation,
            serializers = MainConfig.serializer() to DetailsConfig.serializer(),
            initialPanels = { Panels(main = MainConfig) },
            handleBackButton = true,
            mainFactory = { _, ctx ->
                DefaultMonthTableComponent(
                    context = ctx,
                    daysCoffeesStore = daysCoffeesStore,
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

    override fun onBack() {
        navigation.dismissDetails()
    }

    override fun setMode(mode: ChildPanelsMode) {
        navigation.setMode(mode)
    }
}
