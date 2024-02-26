import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import org.koin.core.context.startKoin
import ru.beryukhov.coffeegram.DefaultPreview
import ru.beryukhov.coffeegram.appModule

private val koinApp = initKoin().koin

private fun initKoin() =
    startKoin {
        modules(appModule())
    }

fun main() = singleWindowApplication(
    title = "CoffeeGram",
    state = WindowState(width = 800.dp, height = 600.dp)
) {
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
