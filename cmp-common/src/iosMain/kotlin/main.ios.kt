import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.Koin
import org.koin.core.context.startKoin
import ru.beryukhov.coffeegram.DefaultPreview
import ru.beryukhov.coffeegram.appModule

val koinApp: Koin by lazy { initKoin().koin }

fun MainViewController() = ComposeUIViewController { DefaultPreview(koinApp.get()) }

private fun initKoin() =
    startKoin {
        modules(appModule())
    }
