package ru.beryukhov.coffeegram.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun GradientTextPreview() {
    AnimatedText(text = "Coffegram", modifier = Modifier)
}

@Composable
internal fun AnimatedText(text: String, modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition()
    val progressAnimation = infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 5000,
                delayMillis = 10,
                easing = LinearEasing
            )
        ),
    )

    Box(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = baseColor,
            modifier = Modifier
                .loadingRevealAnimation(
                    progress = progressAnimation
                ),
        )
    }
}

private val color0 = Color(0x006F4E37) // Transparent Coffee Brown
private val color1 = Color(0x1A6F4E37) // Slightly visible Coffee Brown
private val color2 = Color(0x996F4E37) // Semi-transparent Coffee Brown
private val color3 = Color(0xFFC19A6B) // Solid Light Coffee
private val baseColor = Color(0xFF3B2F2F) // Solid Dark Coffee

private fun Modifier.loadingRevealAnimation(
    progress: State<Float> // -2 .. 2
): Modifier = this
    .graphicsLayer(
        compositingStrategy = CompositingStrategy.Offscreen
    )
    .drawWithContent {
        drawContent()
        drawRect(
            brush = higherPickupFareGradient(progress),
            blendMode = BlendMode.SrcAtop,
        )
    }

internal fun higherPickupFareGradient(progress: State<Float>): ShaderBrush {
    val offset by progress
    return object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            return LinearGradientShader(
                colors = listOf(color0, color1, color2, color3, color2, color1, color0),
                colorStops = listOf(0f, 0.05f, 0.3f, 0.5f, 0.7f, 0.95f, 1f),
                // in design the gradient goes from (60,-1) to (10,9) on viewBox with size (58,27)
                // we map it to the actual view size, then move it's horizontal zero point by offset from -2 to 2 widths
                from = Offset(x = size.width * (60f / 58 + offset), y = size.height * (-1f / 27)),
                to = Offset(x = size.width * (10f / 58 + offset), y = size.height * (9f / 27)),
                tileMode = TileMode.Clamp
            )
        }
    }
}
