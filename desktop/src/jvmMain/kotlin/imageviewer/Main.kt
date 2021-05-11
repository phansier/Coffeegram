package example.imageviewer

import androidx.compose.desktop.DesktopTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import example.imageviewer.utils.Application
import getPreferredWindowSize
import icAppRounded
import ru.beryukhov.coffeegram.DefaultPreview

fun main() = Application {

    val icon = remember(::icAppRounded)
    ComposableWindow(
        title = "CoffeeGram",
        size = getPreferredWindowSize(800, 600),
        undecorated = true,
        icon = icon,
    ) {
        MaterialTheme {
            DesktopTheme {
                DefaultPreview()
            }
        }
    }
}