import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.GlobalContext.startKoin
import ru.beryukhov.coffeegram.DefaultPreview
import ru.beryukhov.coffeegram.appModule

private val koinApp = initKoin().koin

 private fun initKoin() =
    startKoin {
        modules(appModule)
    }

fun main() = singleWindowApplication(
    title = "Coffeegram",
    state = WindowState(width = 800.dp, height = 600.dp),
    icon = TrayIcon
) {
    DevelopmentEntryPoint {
        MaterialTheme {
            CompositionLocalProvider(
                LocalScrollbarStyle provides ScrollbarStyle(
                    minimalHeight = 16.dp,
                    thickness = 8.dp,
                    shape = MaterialTheme.shapes.small,
                    hoverDurationMillis = 300,
                    unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
                )
            ) {
                DefaultPreview(koinApp.get())
            }
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
