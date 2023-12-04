package ru.beryukhov.coffeegram.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.orEmpty
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.resource
import ru.beryukhov.coffeegram.data.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
internal actual fun painterResource(res: String): Painter {
    if (res.endsWith(".xml")) {
        return rememberVectorPainter(icons.get(res)!!)
    }

    return BitmapPainter(resource(res).rememberImageBitmap().orEmpty())
}

// automatically translated from .xml resources using https://github.com/LennartEgb/vec2compose
// TODO: wait for implementation of .xml vector images loading on iOS
val icons = mapOf(
    Res.drawable.cappucino to cappucino,
    Res.drawable.latte to latte,
    Res.drawable.coffee to coffee,
)
