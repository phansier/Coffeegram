package example.imageviewer

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import getPreferredWindowSize
import icAppRounded
import ru.beryukhov.coffeegram.pages.Preview

fun main() {

    Window(
        title = "CoffeeGram",
        size = getPreferredWindowSize(800, 1000),
        icon = icAppRounded()
    ) {
        MaterialTheme {
            DesktopTheme {
                Preview()
            }
        }
    }
}
