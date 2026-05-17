import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import org.koin.core.context.startKoin
import ru.beryukhov.coffeegram.coffeeStorageModule
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.dataStoreModule
import ru.beryukhov.coffeegram.screens.RootScreen

private val koinApp = initKoin().koin

private fun initKoin() =
    startKoin {
        modules(dataStoreModule)
        modules(coffeeStorageModule)
    }

fun MainViewController() = ComposeUIViewController {
    val lifecycle = ApplicationLifecycle()

    val root = DefaultRootComponent(
        DefaultComponentContext(lifecycle = lifecycle),
        themeStore = koinApp.get(),
        daysCoffeesStore = koinApp.get(),
        showMap = true,
    )
    RootScreen(root)
}
