package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

interface RootComponent {
    val pages: Value<ChildPages<*, Child>>
    val themeState: StateFlow<ThemeState>

    fun selectPage(childIndex: Int)

    sealed interface Child {
        class CoffeeEdit(
            val component: CoffeeEditComponent,
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
            initialPages = { Pages(items = listOf(Config.CoffeeEdit, Config.Settings), selectedIndex = 0) },
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
            Config.CoffeeEdit -> RootComponent.Child.CoffeeEdit(
                DefaultCoffeeEditComponent(
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
        data object CoffeeEdit : Config

        @Serializable
        data object Settings : Config
    }
}
