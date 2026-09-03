package ru.beryukhov.coffeegram.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.hasText
import kotlinx.collections.immutable.persistentListOf
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import ru.beryukhov.coffeegram.TestApplication
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.BIG_SQUARE
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.HORIZONTAL_RECTANGLE
import ru.beryukhov.coffeegram.widget.FirstGlanceWidget.Companion.SMALL_SQUARE

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WidgetContentTest {

    private val coffees = persistentListOf(
        WidgetCoffee(CoffeeTypes.Cappuccino, "Cappuccino", 5),
        WidgetCoffee(CoffeeTypes.Latte, "Latte", 4),
        WidgetCoffee(CoffeeTypes.Americano, "Americano", 2),
    )

    @After
    fun tearDown() = stopKoin()

    @Test
    fun smallSizeShowsTotalCupCount() = runGlanceAppWidgetUnitTest {
        setAppWidgetSize(SMALL_SQUARE)

        provideComposable { WithGlanceContext { WidgetContent(coffees, increment = {}, decrement = {}) } }

        onNode(hasText("11")).assertExists()
        onNode(hasText("Cappuccino")).assertDoesNotExist()
    }

    @Test
    fun horizontalSizeShowsOnlyTheMostPopularCoffee() = runGlanceAppWidgetUnitTest {
        setAppWidgetSize(HORIZONTAL_RECTANGLE)

        provideComposable { WithGlanceContext { WidgetContent(coffees, increment = {}, decrement = {}) } }

        onNode(hasText("Cappuccino")).assertExists()
        onNode(hasText("5")).assertExists()
        onNode(hasText("Latte")).assertDoesNotExist()
    }

    @Test
    fun bigSizeShowsEveryCoffee() = runGlanceAppWidgetUnitTest {
        setAppWidgetSize(BIG_SQUARE)

        provideComposable { WithGlanceContext { WidgetContent(coffees, increment = {}, decrement = {}) } }

        onNode(hasText("Cappuccino")).assertExists()
        onNode(hasText("Latte")).assertExists()
        onNode(hasText("Americano")).assertExists()
    }

    @Test
    fun noCoffeeAtAllStillRendersACount() = runGlanceAppWidgetUnitTest {
        setAppWidgetSize(BIG_SQUARE)

        provideComposable { WithGlanceContext { WidgetContent(persistentListOf(), increment = {}, decrement = {}) } }

        onNode(hasText("0")).assertExists()
    }

    @Composable
    private fun WithGlanceContext(content: @Composable () -> Unit) =
        CompositionLocalProvider(LocalContext provides RuntimeEnvironment.getApplication(), content)
}
