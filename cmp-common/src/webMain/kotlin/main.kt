import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.store_lib.Storage

private val appModule = module {
    single<Storage<ThemeState>> {
        ThemeInMemoryStorage()
    }
    single {
        ThemeStore(get())
    }
    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
    single { CoffeeStorage(repository = InMemoryCoffeeRepository()) }
 }

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    lifecycle.attachToDocument()

    ComposeViewport {
        KoinApplication(application = {
            modules(appModule)
        }) {
            val themeStore = koinInject<ThemeStore>()
            val daysCoffeesStore = koinInject<DaysCoffeesStore>()
            val root = remember {
                // withWebHistory { stateKeeper, deepLink ->
                DefaultRootComponent(
                    DefaultComponentContext(lifecycle = lifecycle),
                    themeStore = themeStore,
                    daysCoffeesStore = daysCoffeesStore,
                )
            }
            RootScreen(root)
        }
    }
}

expect fun LifecycleRegistry.attachToDocument()
