package example.imageviewer

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import getPreferredWindowSize
import icAppRounded

fun main() {

    Window(
        title = "ImageViewer",
        size = getPreferredWindowSize(800, 1000),
        icon = icAppRounded()
    ) {
        MaterialTheme {
            DesktopTheme {

            }
        }
    }
}
