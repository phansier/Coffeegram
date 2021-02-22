package example.imageviewer

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import getPreferredWindowSize
import icAppRounded
import ru.beryukhov.coffeegram.DefaultPreview

fun main() {

    Window(
        title = "CoffeeGram",
        size = getPreferredWindowSize(800, 600),
        icon = icAppRounded()
    ) {
        MaterialTheme {
            DesktopTheme {
                DefaultPreview()
            }
        }
    }
}
