package ru.beryukhov.coffeegram.newapp

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.TableScreenIntent
import ru.beryukhov.coffeegram.model.TableScreenState
import ru.beryukhov.coffeegram.model.TableScreenStore
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

interface RootComponent {
    val pages: Value<ChildPages<*, Child>>
    val themeState: StateFlow<ThemeState>

    fun selectPage(childIndex: Int)

    sealed interface Child {
        class Table(
            val component: TableComponent,
        ) : Child

        class Settings(
            val component: SettingsComponent,
        ) : Child
    }
}

class DefaultRootComponent(
    context: ComponentContext,
    val themeStore: ThemeStore,
    val daysCoffeesStore: DaysCoffeesStore,
) : RootComponent, ComponentContext by context {
    private val navigation = PagesNavigation<Config>()

    override val pages: Value<ChildPages<*, RootComponent.Child>> =
        childPages(
            source = navigation,
            serializer = Config.serializer(),
            initialPages = { Pages(items = listOf(Config.Table, Config.Settings), selectedIndex = 0) },
            handleBackButton = true,
            childFactory = ::child,
        )

    override val themeState: StateFlow<ThemeState> = themeStore.state

    override fun selectPage(childIndex: Int) {
        navigation.select(childIndex)
    }

    private fun child(
        config: Config,
        context: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.Table -> RootComponent.Child.Table(
                DefaultTableComponent(
                    context = context,
                    daysCoffeesStore = daysCoffeesStore,
                )
            )

            Config.Settings -> RootComponent.Child.Settings(
                DefaultSettingsComponent(
                    context = context,
                    themeStore = themeStore,
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Table : Config

        @Serializable
        data object Settings : Config
    }
}

interface TableComponent {

    val models: StateFlow<TableScreenState>

    fun onIncrementMonth()
    fun onDecrementMonth()
    fun onDayClick(dayOfMonth: Int)

    // fun onNavigate(child: KClass<out RootComponent.Child>)
}

class DefaultTableComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
    val tableScreenStore: TableScreenStore = TableScreenStore(
        initialStoreState = daysCoffeesStore.state.value
    ), // todo move into DI
) : TableComponent, ComponentContext by context {

    override val models: StateFlow<TableScreenState> = tableScreenStore.state

    init {
        daysCoffeesStore.state.onEach {
            tableScreenStore.newIntent(TableScreenIntent.NewDaysCoffeesState(it))
        }.launchIn(CoroutineScope(Dispatchers.Default + SupervisorJob()))
    }

    override fun onIncrementMonth() {
        tableScreenStore.newIntent(TableScreenIntent.NextMonth)
    }

    override fun onDecrementMonth() {
        tableScreenStore.newIntent(TableScreenIntent.PreviousMonth)
    }

    override fun onDayClick(dayOfMonth: Int) {
        println("onDayClick $dayOfMonth")
    }

//    override fun onNavigate(child: KClass<out RootComponent.Child>) {}
}

interface SettingsComponent {
    val models: StateFlow<ThemeState>

    fun onSetSystemTheme()
    fun onSetLightTheme()
    fun onSetDarkTheme()

    fun onSetCupertinoTheme(enabled: Boolean)
}

class DefaultSettingsComponent(
    context: ComponentContext,
    val themeStore: ThemeStore,
) : SettingsComponent, ComponentContext by context {
    override val models: StateFlow<ThemeState> = themeStore.state

    override fun onSetSystemTheme() {
        themeStore.newIntent(ThemeIntent.SetSystemIntent)
    }

    override fun onSetLightTheme() {
        themeStore.newIntent(ThemeIntent.SetLightIntent)
    }

    override fun onSetDarkTheme() {
        themeStore.newIntent(ThemeIntent.SetDarkIntent)
    }

    override fun onSetCupertinoTheme(enabled: Boolean) {
        themeStore.newIntent(ThemeIntent.SetCupertinoIntent(enabled))
    }
}
