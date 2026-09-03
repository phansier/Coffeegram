package ru.beryukhov.coffeegram.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.beryukhov.coffeegram.TestApplication
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class WidgetPreviewCoffeesTest {

    private val allZero = persistentListOf(WidgetCoffee(CoffeeTypes.Cappuccino, "Cappuccino", 0))

    @After
    fun tearDown() = stopKoin()

    @Test
    fun usesTodaysDataWhenSomethingWasDrunk() = runBlocking {
        val today = persistentListOf(WidgetCoffee(CoffeeTypes.Americano, "Americano", 4))

        assertEquals(today, bridgeOf(flowOf(today)).previewCoffees())
    }

    @Test
    fun waitsPastTheEmptyStateTheStoreStartsWith() = runBlocking {
        val today = persistentListOf(WidgetCoffee(CoffeeTypes.Americano, "Americano", 4))

        val coffees = bridgeOf(flowOf(allZero, today)).previewCoffees()

        assertEquals(today, coffees)
    }

    @Test
    fun fallsBackToTheSampleWhenNothingWasDrunk() = runBlocking {
        val coffees = bridgeOf(flowOf(allZero)).previewCoffees()

        assertEquals(widgetPreviewCounts.map { it.coffee }, coffees.map { it.type })
    }

    @Test
    fun fallsBackToTheSampleWhenTheStoreNeverLoads() = runBlocking {
        val coffees = bridgeOf(emptyFlow()).previewCoffees()

        assertEquals(widgetPreviewCounts.map { it.coffee }, coffees.map { it.type })
    }

    private fun bridgeOf(days: Flow<PersistentList<WidgetCoffee>>) = object : WidgetDataBridge {
        override fun getCurrentDayList(): Flow<PersistentList<WidgetCoffee>> = days
        override fun incrementCoffee(coffeeType: CoffeeType) = Unit
        override fun decrementCoffee(coffeeType: CoffeeType) = Unit
    }
}
