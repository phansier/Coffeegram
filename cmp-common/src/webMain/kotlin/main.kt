import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration
import ru.beryukhov.coffeegram.coffeeStorageModule
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.dataStoreModule
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.screens.RootScreen

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    lifecycle.attachToDocument()

    ComposeViewport {
        // withWebHistory { stateKeeper, deepLink ->
        KoinApplication(
            configuration = koinConfiguration(
                declaration = {
                    modules(coffeeStorageModule)
                    modules(dataStoreModule)
                }
            ),
            content = {
                val themeStore = koinInject<ThemeStore>()
                val daysCoffeesStore = koinInject<DaysCoffeesStore>()
                val root = remember {
                    // withWebHistory { stateKeeper, deepLink ->
                    DefaultRootComponent(
                        DefaultComponentContext(lifecycle = lifecycle),
                        themeStore = themeStore,
                        daysCoffeesStore = daysCoffeesStore,
                        showMap = true
                    )
                }
                RootScreen(root)
            }
        )
    }
}

expect fun LifecycleRegistry.attachToDocument()
