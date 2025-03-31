package ru.beryukhov.coffeegram.data

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

sealed interface Picture {
    @Composable
    fun painter(): Painter

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
    )

    companion object {
        val EMPTY: Picture = Icon(
            Icons.Default.Delete,
            tint = Color.Transparent
        )
    }
}

data class Image(val iconRes: DrawableResource) : Picture {
    @Composable
    override fun painter(): Painter = painterResource(iconRes)

    @Composable
    override fun invoke(modifier: Modifier, contentDescription: String?) = Image(
        painter = painter(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}

data class Icon(val image: ImageVector, val tint: Color? = null) : Picture {
    @Composable
    override fun painter(): Painter = rememberVectorPainter(image)

    @Composable
    override fun invoke(modifier: Modifier, contentDescription: String?) = Icon(
        painter = painter(),
        contentDescription = contentDescription,
        tint = tint ?: LocalContentColor.current,
        modifier = modifier
    )
}

data class Coil(val url: String) : Picture {

    @Composable
    override fun painter(): Painter = rememberAsyncImagePainter(url)

    @Composable
    override fun invoke(modifier: Modifier, contentDescription: String?) =
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
        )
}
