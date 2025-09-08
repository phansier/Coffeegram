import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.w3c.dom.Document

actual fun LifecycleRegistry.attachToDocument() {

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

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(document) => document.visibilityState")
private external fun visibilityState(document: Document): String
