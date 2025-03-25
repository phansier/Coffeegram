import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.GlobalContext.startKoin
import ru.beryukhov.coffeegram.appModule
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.screens.RootScreen

private val koinApp = initKoin().koin

private fun initKoin() =
    startKoin {
        modules(appModule)
    }

fun main() {
    val lifecycle = LifecycleRegistry()

    val root =
        DefaultRootComponent(
            DefaultComponentContext(lifecycle = lifecycle),
            themeStore = koinApp.get(),
            daysCoffeesStore = koinApp.get(),
        )

    singleWindowApplication(
        title = "Coffeegram",
        state = WindowState(width = 800.dp, height = 600.dp),
        icon = TrayIcon
    ) {
        DevelopmentEntryPoint {
            RootScreen(root)
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
