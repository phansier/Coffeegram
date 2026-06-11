import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.core.context.GlobalContext.startKoin
import ru.beryukhov.coffeegram.coffeeStorageModule
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.dataStoreModule
import ru.beryukhov.coffeegram.screens.RootScreen
import javax.swing.SwingUtilities

private val koinApp = initKoin().koin

private fun initKoin() =
    startKoin {
        modules(dataStoreModule)
        modules(coffeeStorageModule)
    }

fun main() {
    val lifecycle = LifecycleRegistry()

    // Decompose requires its component tree to be created on the UI (Swing EDT) thread,
    // which is Compose Desktop's main thread. Creating it on the raw `main` thread throws
    // NotOnMainThreadException, so hop onto the EDT for construction.
    val root = runOnUiThread {
        DefaultRootComponent(
            DefaultComponentContext(lifecycle = lifecycle),
            themeStore = koinApp.get(),
            daysCoffeesStore = koinApp.get(),
        )
    }

    singleWindowApplication(
        title = "Coffeegram",
        state = WindowState(width = 800.dp, height = 600.dp),
        icon = TrayIcon
    ) {
        RootScreen(root)
    }
}

private fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) return block()

    var result: T? = null
    SwingUtilities.invokeAndWait { result = block() }

    @Suppress("UNCHECKED_CAST")
    return result as T
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
