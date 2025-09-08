import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import web.dom.DocumentVisibilityState
import web.dom.document
import web.dom.visible
import web.events.EventType
import web.events.addEventListener

actual fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (document.visibilityState == DocumentVisibilityState.visible) {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(
        type = EventType("visibilitychange"),
        handler = { onVisibilityChanged() }
    )
}
