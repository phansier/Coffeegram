package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.childPagesWebNavigation
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

@OptIn(ExperimentalDecomposeApi::class)
interface RootComponent : WebNavigationOwner {
    val pages: Value<ChildPages<*, Child>>
    val themeState: StateFlow<ThemeState>

    val showMap: Boolean

    fun selectPage(childIndex: Int)

    sealed interface Child {
        class CoffeeEdit(
            val component: CoffeeEditComponent,
        ) : Child

        class Stats(
            val component: StatsComponent,
        ) : Child

        class Map(
            val component: MapComponent,
        ) : Child

        class Settings(
            val component: SettingsComponent,
        ) : Child
    }
}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultRootComponent(
    context: ComponentContext,
    val themeStore: ThemeStore,
    val daysCoffeesStore: DaysCoffeesStore,
    override val showMap: Boolean = false,
    deepLink: String? = null,
    private val onAndroidStartWearableActivity: StateFlow<(() -> Unit)?> = MutableStateFlow(null),
    private val onAndroidIconChange: (isSummer: Boolean) -> Unit = {},
    ) : RootComponent, ComponentContext by context {
    private val navigation = PagesNavigation<Config>()

    private val deepLinkSegments: List<String> = deepLink?.pathSegments().orEmpty()

    private val typedPages: Value<ChildPages<Config, RootComponent.Child>> =
        childPages(
            source = navigation,
            serializer = Config.serializer(),
            initialPages = {
                val items = buildList {
                    add(Config.CoffeeEdit)
                    add(Config.Stats)
                    if (showMap) add(Config.Map)
                    add(Config.Settings)
                }
                Pages(
                    items = items,
                    selectedIndex = items.indexOfFirst { it.path == deepLinkSegments.firstOrNull() }
                        .coerceAtLeast(0),
                )
            },
            childFactory = ::child,
        )

    override val pages: Value<ChildPages<*, RootComponent.Child>> = typedPages

    override val webNavigation: WebNavigation<*> =
        childPagesWebNavigation(
            navigator = navigation,
            pages = typedPages,
            serializer = Config.serializer(),
            pathMapper = { state -> state.items.getOrNull(state.selectedIndex)?.configuration?.path },
            childSelector = { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.CoffeeEdit -> instance.component
                    else -> null
                }
            },
        )

    override val themeState: StateFlow<ThemeState> = themeStore.state

    override fun selectPage(childIndex: Int) {
        navigation.select(childIndex)
    }

    private fun nestedDeepLinkSegments(config: Config): List<String> =
        if (deepLinkSegments.firstOrNull() == config.path) deepLinkSegments.drop(1) else emptyList()

    private fun child(
        config: Config,
        context: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.CoffeeEdit -> RootComponent.Child.CoffeeEdit(
                DefaultCoffeeEditComponent(
                    context = context,
                    daysCoffeesStore = daysCoffeesStore,
                    deepLinkSegments = nestedDeepLinkSegments(config),
                )
            )

            Config.Stats -> RootComponent.Child.Stats(
                DefaultStatsComponent(
                    context = context,
                    daysCoffeesStore = daysCoffeesStore,
                )
            )

            Config.Map -> RootComponent.Child.Map(
                DefaultMapComponent(
                    context = context,
                )
            )

            Config.Settings -> RootComponent.Child.Settings(
                DefaultSettingsComponent(
                    context = context,
                    themeStore = themeStore,
                    onAndroidStartWearableActivity = onAndroidStartWearableActivity,
                    onAndroidIconChange = onAndroidIconChange,
                )
            )
        }

    @Serializable
    private sealed interface Config {
        val path: String

        @Serializable
        data object CoffeeEdit : Config {
            override val path: String get() = "calendar"
        }

        @Serializable
        data object Stats : Config {
            override val path: String get() = "stats"
        }

        @Serializable
        data object Map : Config {
            override val path: String get() = "map"
        }

        @Serializable
        data object Settings : Config {
            override val path: String get() = "settings"
        }
    }
}

internal fun String.pathSegments(): List<String> =
    substringAfter("://")
        .substringAfter('/', missingDelimiterValue = "")
        .substringBefore('?')
        .substringBefore('#')
        .split('/')
        .filter(String::isNotEmpty)
