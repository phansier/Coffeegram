import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration
import ru.beryukhov.coffeegram.admin.AdminApp
import ru.beryukhov.coffeegram.admin.isAdminRoute
import ru.beryukhov.coffeegram.coffeeStorageModule
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.dataStoreModule
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.screens.RootScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    lifecycle.attachToDocument()

    ComposeViewport {
        if (isAdminRoute()) {
            AdminApp()
        } else {
            CoffeegramApp(lifecycle)
        }
    }
}

@Composable
private fun CoffeegramApp(lifecycle: LifecycleRegistry) {
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
                withWebHistory { stateKeeper, _ ->
                    DefaultRootComponent(
                        context = DefaultComponentContext(
                            lifecycle = lifecycle,
                            stateKeeper = stateKeeper,
                        ),
                        themeStore = themeStore,
                        daysCoffeesStore = daysCoffeesStore,
                        showMap = true,
                    )
                }
            }
            RootScreen(root)
        }
    )
}

expect fun LifecycleRegistry.attachToDocument()
