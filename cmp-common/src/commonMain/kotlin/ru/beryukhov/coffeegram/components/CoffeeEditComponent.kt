package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

interface CoffeeEditComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class MonthTable(
            val component: MonthTableComponent,
        ) : Child

        class DayList(
            val component: DayListComponent,
        ) : Child
    }
}

class DefaultCoffeeEditComponent(
    context: ComponentContext,
    val daysCoffeesStore: DaysCoffeesStore,
) : CoffeeEditComponent, ComponentContext by context {
    private val navigation = StackNavigation<Config>()
    override val childStack: Value<ChildStack<*, CoffeeEditComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.MonthTable,
            childFactory = ::child,
        )

    private fun child(
        config: Config,
        context: ComponentContext,
    ): CoffeeEditComponent.Child =
        when (config) {
            Config.MonthTable -> CoffeeEditComponent.Child.MonthTable(
                DefaultMonthTableComponent(
                    context = context,
                    daysCoffeesStore = daysCoffeesStore,
                    onNavigate = {
                        val screen = Config.DayList(it)
                        navigation.pushNew(screen)
                    }
                )
            )
            is Config.DayList -> CoffeeEditComponent.Child.DayList(
                DefaultDayListComponent(
                    context = context,
                    daysCoffeesStore = daysCoffeesStore,
                    date = config.date,
                    onBackNavigation = {
                        navigation.pop()
                    }
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object MonthTable : Config

        @Serializable
        data class DayList(val date: LocalDate) : Config
    }
}
