@file:Suppress("MaxLineLength")

package ru.beryukhov.coffeegram.data.icons

import androidx.compose.ui.graphics.vector.ImageVector

val CoffeeIcons.Cocoa: ImageVector
    get() = cachedCocoa ?: coffeeIcon(
        name = "Cocoa",
        width = 81f,
        height = 106f,
    ) {
        fill(0xFFCCC9C9, "M40.867,22.215H0.984L14.859,105.449H66.879L80.75,22.215")
        fill(0xFFAA6D44, "M40.867,25.242H8.258L19.602,91.93H62.137L73.477,25.242")
        fill(0xFF33251C, "M54.605,24.227L67.203,0.105L74.098,2.613L61.418,27.727C60.77,29.016 59.445,29.828 58,29.828C55.125,29.828 53.277,26.777 54.605,24.227Z")
        fill(0xFF5B4638, "M71.738,3.797L58.531,29.793L69.133,4.531L65.57,3.23L66.398,1.645")
        fill(0xFFD59363, "M73.477,25.242H8.258L10.656,39.336H70.691L73.477,25.242Z")
        fill(0xFFFFF9F9, "M80.75,22.215L66.879,105.449H59.879L73.477,22.215H80.75Z")
    }.also { cachedCocoa = it }

private var cachedCocoa: ImageVector? = null
