package ru.beryukhov.coffeegram.data.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

object CoffeeIcons

internal fun coffeeIcon(
    name: String,
    width: Float,
    height: Float,
    viewportWidth: Float = width,
    viewportHeight: Float = height,
    paths: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = width.dp,
    defaultHeight = height.dp,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
).apply(paths).build()

internal fun ImageVector.Builder.fill(color: Long, pathData: String) {
    addPath(pathData = addPathNodes(pathData), fill = SolidColor(Color(color)))
}
