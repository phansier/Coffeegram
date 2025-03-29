import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.w3c.dom.Document
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.store_lib.Storage

private val appModule = module {
    single<Storage<ThemeState>> {
        LocalThemePrefStorage()
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

    val title = "Coffeegram"
    CanvasBasedWindow(title, canvasElementId = "ComposeTarget") {
        KoinApplication(application = {
            modules(appModule)
        }) {
            val themeStore = koinInject<ThemeStore>()
            val daysCoffeesStore = koinInject<DaysCoffeesStore>()
            val root = remember {
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

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (visibilityState(document) == "visible") {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}

@JsFun("(document) => document.visibilityState")
private external fun visibilityState(document: Document): String

class LocalThemePrefStorage : Storage<ThemeState> {
    private var themeState: ThemeState? = null

    override suspend fun getState(): ThemeState? = themeState
    override suspend fun saveState(state: ThemeState) {
        themeState = state
    }
}
