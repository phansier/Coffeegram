import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.w3c.dom.Document
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.newapp.DefaultRootComponent
import ru.beryukhov.coffeegram.newapp.NewApp
import ru.beryukhov.coffeegram.repository.CoffeeStorage

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root = DefaultRootComponent(
            DefaultComponentContext(lifecycle = lifecycle),
            themeStore = ThemeStore(LocalThemePrefStorage()),
            daysCoffeesStore = DaysCoffeesStoreImpl(CoffeeStorage(InMemoryCoffeeRepository())),
        )

    lifecycle.attachToDocument()

    ComposeViewport(document.body!!) {
        NewApp(root)
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
