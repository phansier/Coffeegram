package ru.beryukhov.coffeegram.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.Lifecycle.State
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
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
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.model.getNavBarItems
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import kotlin.time.Clock

@StorePreview
@Composable
private fun MonthTableScreenPreview() = ScreenPreview(
    headlineLines = listOf("Track your coffee habits and discover new flavors"),
    backgroundColor = Color(0xFFDCA865),
    pageNum = 0,
)

private val coffeeEditComponent
    get() = object : CoffeeEditComponent {
        override val childStack: Value<ChildStack<DefaultCoffeeEditComponent.Config, CoffeeEditComponent.Child>>
            get() = MutableValue<ChildStack<DefaultCoffeeEditComponent.Config, CoffeeEditComponent.Child>>(
                ChildStack(
                    configuration = DefaultCoffeeEditComponent.Config.DayList(
                        Clock.System.todayIn(TimeZone.currentSystemDefault())
                    ),
                    instance = CoffeeEditComponent.Child.DayList(
                        DefaultDayListComponent(
                            context = DefaultComponentContext(object : Lifecycle {
                                override val state: State = State.RESUMED
                                override fun subscribe(callbacks: Lifecycle.Callbacks) = Unit
                                override fun unsubscribe(callbacks: Lifecycle.Callbacks) = Unit
                            }),
                            daysCoffeesStore = DaysCoffeesStoreImpl(
                                CoffeeStorage(repository = InMemoryCoffeeRepository())
                            ),
                            date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                            onBackNavigation = {},
                        )
                    )
                )
            )
    }

@StorePreview
@Composable
private fun ListScreenPreview() = PreviewTheme {
    StoreMarketingScreen(
        headlineLines = listOf("Track your coffee habits and discover new flavors"),
        backgroundColor = Color(0xFFDEB784),
    ) {
        RootScreen(
            pageNum = 0,
            TopBar = {
                CoffeeEditAppBar(coffeeEditComponent = coffeeEditComponent)
            },
            CurrentScreen = { CoffeeEditScreen(coffeeEditComponent) },
        )
    }
}

@StorePreview
@Composable
private fun StatsScreenPreview() = ScreenPreview(
    headlineLines = listOf("Discover your coffee preferences and explore new flavors"),
    backgroundColor = Color(0xFFBFA6A0),
    pageNum = 1,
)

@StorePreview
@Composable
private fun SettingsScreenPreview() = ScreenPreview(
    headlineLines = listOf("Customize your coffee experience and explore new flavors"),
    backgroundColor = Color(0xFFBFA6A0),
    pageNum = 2,
)

@Composable
private fun ScreenPreview(
    headlineLines: List<String>,
    subheadline: String? = null,
    backgroundColor: Color,
    pageNum: Int,
) = PreviewTheme {
    StoreMarketingScreen(
        headlineLines = headlineLines,
        subheadline = subheadline,
        backgroundColor = backgroundColor,
    ) {
        RootScreen(
            rootComponent = DefaultRootComponent(
                context = DefaultComponentContext(object : Lifecycle {
                    override val state: State = State.RESUMED
                    override fun subscribe(callbacks: Lifecycle.Callbacks) = Unit
                    override fun unsubscribe(callbacks: Lifecycle.Callbacks) = Unit
                }),
                themeStore = ThemeStore(ThemeInMemoryStorage()),
                daysCoffeesStore = DaysCoffeesStoreImpl(CoffeeStorage(repository = InMemoryCoffeeRepository()))
            ).apply { selectPage(pageNum) },
        )
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
private fun RootScreen(
    pageNum: Int,
    TopBar: @Composable (Modifier) -> Unit = { _ -> },
    CurrentScreen: @Composable () -> Unit = { },
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
            CurrentScreen()
        }
    }
}
