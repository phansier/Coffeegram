package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.Lifecycle.State
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.app_ui.StoreMarketingScreen
import ru.beryukhov.coffeegram.app_ui.StorePreview
import ru.beryukhov.coffeegram.components.CoffeeEditComponent
import ru.beryukhov.coffeegram.components.DefaultCoffeeEditComponent
import ru.beryukhov.coffeegram.components.DefaultDayListComponent
import ru.beryukhov.coffeegram.components.DefaultMonthTableComponent
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.components.DefaultStatsComponent
import ru.beryukhov.coffeegram.components.StatsComponent
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.model.getNavBarItems
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import kotlin.time.Clock

@StorePreview
@Composable
private fun MonthTableScreenPreview() = PreviewTheme {
    val component = monthTableCoffeeEditComponent
    StoreMarketingScreen(
        headlineLines = listOf("Track your coffee habits day by day"),
        backgroundColor = Color(0xFFDCA865),
    ) {
        RootScreen(
            pageNum = 0,
            TopBar = { CoffeeEditAppBar(coffeeEditComponent = component) },
            CurrentScreen = { padding -> CoffeeEditScreen(component, contentPadding = padding) },
        )
    }
}

@StorePreview
@Composable
private fun ListScreenPreview() = PreviewTheme {
    val component = dayListCoffeeEditComponent
    StoreMarketingScreen(
        headlineLines = listOf("Log every cup you brewed today"),
        backgroundColor = Color(0xFFDEB784),
    ) {
        RootScreen(
            pageNum = 0,
            TopBar = { CoffeeEditAppBar(coffeeEditComponent = component) },
            CurrentScreen = { padding -> CoffeeEditScreen(component, contentPadding = padding) },
        )
    }
}

private val previewLifecycle = object : Lifecycle {
    override val state: State = State.RESUMED
    override fun subscribe(callbacks: Lifecycle.Callbacks) = Unit
    override fun unsubscribe(callbacks: Lifecycle.Callbacks) = Unit
}

private val previewDaysCoffeesStore: DaysCoffeesStore
    get() = object : DaysCoffeesStore {
        override val state: StateFlow<DaysCoffeesState> = MutableStateFlow(sampleDaysCoffeesState())
        override fun newIntent(intent: DaysCoffeesIntent) = Unit
    }

private fun sampleDaysCoffeesState(): DaysCoffeesState {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    fun day(d: Int): LocalDate = LocalDate(today.year, today.month, d)
    // Seed every visible calendar week with at least one entry. `today` goes last so
    // it overrides if it happens to fall on one of the seeded days.
    return DaysCoffeesState(
        coffees = mapOf(
            day(4) to DayCoffee(mapOf(CoffeeTypes.Espresso to 2)),
            day(7) to DayCoffee(mapOf(CoffeeTypes.Latte to 1)),
            day(11) to DayCoffee(mapOf(CoffeeTypes.Mocha to 1, CoffeeTypes.Macchiato to 1)),
            day(14) to DayCoffee(mapOf(CoffeeTypes.Cappuccino to 1)),
            day(18) to DayCoffee(mapOf(CoffeeTypes.Glace to 1, CoffeeTypes.Frappe to 1)),
            day(22) to DayCoffee(mapOf(CoffeeTypes.Americano to 1)),
            day(26) to DayCoffee(mapOf(CoffeeTypes.Latte to 2)),
            today to DayCoffee(
                mapOf(
                    CoffeeTypes.Cappuccino to 1,
                    CoffeeTypes.Americano to 2,
                    CoffeeTypes.Latte to 1,
                )
            ),
        )
    )
}

private val monthTableCoffeeEditComponent: CoffeeEditComponent
    get() = previewCoffeeEditComponent(
        configuration = DefaultCoffeeEditComponent.Config.MonthTable,
        instance = { store ->
            CoffeeEditComponent.Child.MonthTable(
                DefaultMonthTableComponent(
                    context = DefaultComponentContext(previewLifecycle),
                    daysCoffeesStore = store,
                    onNavigate = {},
                )
            )
        },
    )

private val dayListCoffeeEditComponent: CoffeeEditComponent
    get() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return previewCoffeeEditComponent(
            configuration = DefaultCoffeeEditComponent.Config.DayList(today),
            instance = { store ->
                CoffeeEditComponent.Child.DayList(
                    DefaultDayListComponent(
                        context = DefaultComponentContext(previewLifecycle),
                        daysCoffeesStore = store,
                        date = today,
                        onBackNavigation = {},
                    )
                )
            },
        )
    }

private fun previewCoffeeEditComponent(
    configuration: DefaultCoffeeEditComponent.Config,
    instance: (DaysCoffeesStore) -> CoffeeEditComponent.Child,
): CoffeeEditComponent = object : CoffeeEditComponent {
    override val childStack: Value<ChildStack<DefaultCoffeeEditComponent.Config, CoffeeEditComponent.Child>> =
        MutableValue(
            ChildStack(
                configuration = configuration,
                instance = instance(previewDaysCoffeesStore),
            )
        )

    override val backHandler: BackHandler = BackDispatcher()

    override fun onBack() = Unit
}

@StorePreview
@Composable
private fun StatsScreenPreview() = PreviewTheme {
    val component = previewStatsComponent
    StoreMarketingScreen(
        headlineLines = listOf("Discover trends across every cup you brewed"),
        backgroundColor = Color(0xFFBFA6A0),
    ) {
        RootScreen(
            pageNum = 1,
            TopBar = { StatsAppBar() },
            CurrentScreen = { padding -> StatsScreen(component, modifier = Modifier.padding(padding)) },
        )
    }
}

private val previewStatsComponent: StatsComponent
    get() = DefaultStatsComponent(
        context = DefaultComponentContext(previewLifecycle),
        daysCoffeesStore = previewDaysCoffeesStore,
    )

// @StorePreview
// @Composable
// private fun SettingsScreenPreview() = ScreenPreview(
//     headlineLines = listOf("Customize your coffee experience and explore new flavors"),
//     backgroundColor = Color(0xFFBFA6A0),
//     pageNum = 2,
// )

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
private fun RootScreen(
    pageNum: Int,
    TopBar: @Composable (Modifier) -> Unit = { _ -> },
    CurrentScreen: @Composable (PaddingValues) -> Unit = { },
    modifier: Modifier = Modifier,
) {
    val rootComponent = remember {
        DefaultRootComponent(
            context = DefaultComponentContext(object : Lifecycle {
                override val state: State = State.RESUMED
                override fun subscribe(callbacks: Lifecycle.Callbacks) = Unit
                override fun unsubscribe(callbacks: Lifecycle.Callbacks) = Unit
            }),
            themeStore = ThemeStore(ThemeInMemoryStorage()),
            daysCoffeesStore = DaysCoffeesStoreImpl(CoffeeStorage(repository = InMemoryCoffeeRepository()))
        ).apply {
            selectPage(pageNum)
        }
    }
    val navBarItems = remember { getNavBarItems() }
    CoffeegramTheme(
        themeState = rootComponent.themeState.collectAsState().value,
    ) {
        AdaptiveScaffold(
            modifier = Modifier,
            topBar = { TopBar(modifier) },
            bottomBar = { BottomBar(rootComponent, navBarItems) }
        ) { paddingValues ->
            CurrentScreen(paddingValues)
        }
    }
}
