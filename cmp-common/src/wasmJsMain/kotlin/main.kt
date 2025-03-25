import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.w3c.dom.Document
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.store_lib.Storage

// koin causes RuntimeError: dereferencing a null pointer
// https://github.com/InsertKoinIO/koin/issues/1983
// private val koinApp = initKoin().koin
//
// private val appModule = module {
//
//    single<Storage<ThemeState>> {
//        LocalThemePrefStorage()
//    }
//    single {
//        ThemeStore(get())
//    }
//    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
//    single { CoffeeStorage(repository = InMemoryCoffeeRepository()) }
//
//    single { NavigationStore() }
//    single { Dependencies(get(), get(), get()) }
// }
//
// private fun initKoin() =
//    startKoin {
//        modules(appModule)
//    }

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

//    val root = DefaultRootComponent(
//            DefaultComponentContext(lifecycle = lifecycle),
//            themeStore = koinApp.get(),
//            daysCoffeesStore = koinApp.get(),
//        )
    val root = DefaultRootComponent(
            DefaultComponentContext(lifecycle = lifecycle),
            themeStore = ThemeStore(LocalThemePrefStorage()),
            daysCoffeesStore = DaysCoffeesStoreImpl(CoffeeStorage(InMemoryCoffeeRepository())),
        )

    lifecycle.attachToDocument()

    ComposeViewport(document.body!!) {
        RootScreen(root)
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
