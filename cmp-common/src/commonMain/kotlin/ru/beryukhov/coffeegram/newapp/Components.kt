package ru.beryukhov.coffeegram.newapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable

interface RootComponent {
    val pages: Value<ChildPages<*, Child>>
    val isMaterial: State<Boolean>
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
) : RootComponent, ComponentContext by context {
    private val navigation = PagesNavigation<Config>()
    private val model =
        instanceKeeper.getOrCreate {
            RootViewModel()
        }

    override val pages: Value<ChildPages<*, RootComponent.Child>> =
        childPages(
            source = navigation,
            serializer = Config.serializer(),
            initialPages = { Pages(items = listOf(Config.Table,Config.Settings), selectedIndex = 0) },
            handleBackButton = true,
            childFactory = ::child,
        )

    override val isMaterial: State<Boolean>
        get() = model.isMaterial

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
                    isMaterial = model.isMaterial,
                )
            )
            Config.Settings -> RootComponent.Child.Settings(
                DefaultSettingsComponent(
                    context = context,
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

class RootViewModel : InstanceKeeper.Instance {
    val isMaterial = mutableStateOf(false)
}

interface TableComponent {
    val isMaterial: State<Boolean>

    fun onThemeChanged()

    // fun onNavigate(child: KClass<out RootComponent.Child>)
}

class DefaultTableComponent(
    context: ComponentContext,
    override val isMaterial: MutableState<Boolean>,
) : TableComponent, ComponentContext by context {

    override fun onThemeChanged() {
        isMaterial.value = !isMaterial.value
    }

//    override fun onNavigate(child: KClass<out RootComponent.Child>) {}
}

interface SettingsComponent {
}

class DefaultSettingsComponent(
    context: ComponentContext,
) : SettingsComponent, ComponentContext by context {

}
