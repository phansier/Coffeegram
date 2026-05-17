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

// Built once for the lifetime of the iOS app process. Reusing the same root across
// `ComposeUIViewController` re-evaluations (e.g. light/dark toggle) preserves the
// Decompose navigation stack.
private val rootComponent: DefaultRootComponent by lazy {
    DefaultRootComponent(
        DefaultComponentContext(lifecycle = ApplicationLifecycle()),
        themeStore = koinApp.get(),
        daysCoffeesStore = koinApp.get(),
        showMap = true,
    )
}

fun MainViewController() = ComposeUIViewController {
    RootScreen(rootComponent)
}
