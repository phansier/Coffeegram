package ru.beryukhov.coffeegram.newapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    val isMaterial: State<Boolean>

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
    private val navigation = StackNavigation<Config>()
    private val model =
        instanceKeeper.getOrCreate {
            RootViewModel()
        }

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Table,
            handleBackButton = true,
            childFactory = ::child,
        )

    override val isMaterial: State<Boolean>
        get() = model.isMaterial

    private fun child(
        config: Config,
        context: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.Table -> RootComponent.Child.Table(
                DefaultTableComponent(
                    context = context,
                    isMaterial = model.isMaterial,
                    onNavigate = {
                        val screen =
                            when (it) {
                                RootComponent.Child.Table::class -> Config.Table // todo actual child screens
                                RootComponent.Child.Settings::class -> Config.Settings
                                else -> return@DefaultTableComponent
                            }
                        navigation.pushNew(screen)
                    }
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

    fun onNavigate(child: KClass<out RootComponent.Child>)
}

class DefaultTableComponent(
    context: ComponentContext,
    override val isMaterial: MutableState<Boolean>,
    private val onNavigate: (KClass<out RootComponent.Child>) -> Unit,
) : TableComponent, ComponentContext by context {

    override fun onThemeChanged() {
        isMaterial.value = !isMaterial.value
    }

    override fun onNavigate(child: KClass<out RootComponent.Child>) {
        onNavigate.invoke(child)
    }
}

interface SettingsComponent {
}

class DefaultSettingsComponent(
    context: ComponentContext,
) : SettingsComponent, ComponentContext by context {

}
