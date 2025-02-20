import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.w3c.dom.Document
import ru.beryukhov.coffeegram.newapp.DefaultRootComponent
import ru.beryukhov.coffeegram.newapp.NewApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root =
        DefaultRootComponent(
            DefaultComponentContext(lifecycle = lifecycle),
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
