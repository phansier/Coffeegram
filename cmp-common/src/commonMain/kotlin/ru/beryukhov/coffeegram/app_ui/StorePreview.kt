package ru.beryukhov.coffeegram.app_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// kudos to https://medium.com/@benlue/generate-app-store-screenshots-directly-from-jetpack-compose-previews-b2e30e4569a7
// 1290x2796 at 2.5x scale = 516dp x 1118dp
private const val widthDp = 516
private const val heightDp = 1118

@Preview(name = "en-mobile", locale = "en", widthDp = widthDp, heightDp = heightDp)
@Preview(name = "en-tablet", locale = "en", device = Devices.PIXEL_C)
annotation class StorePreview

@Composable
fun PhoneFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 40.dp,
                shape = RoundedCornerShape(44.dp),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(44.dp))
            .background(Color(0xFF1C1C1E)) // Device bezel
            .border(12.dp, Color(0xFF2C2C2E), RoundedCornerShape(44.dp))
            .padding(12.dp)
    ) {
        ScreenContent(content)
        DynamicIsland()
    }
}

@Composable
private fun ScreenContent(content: @Composable (() -> Unit)) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
    ) {
        content()
    }
}

@Composable
private fun BoxScope.DynamicIsland() {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 14.dp)
            .size(width = 100.dp, height = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
    )
}

@Composable
@StorePreview
fun PhoneFramePreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PhoneFrame {
            ScreenContentPlaceholder()
        }
    }
}

@Composable
private fun ScreenContentPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E5EA)) // Light gray screen background
    )
}

@Composable
fun StoreMarketingScreen(
    headlineLines: List<String>,
    subheadline: String? = null,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.75f)
                    )
                )
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            headlineLines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            subheadline?.let {
                Text(
                    text = subheadline.uppercase(),
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            PhoneFrame(
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 64.dp, end = 64.dp)
            ) { content() }
        }
    }
}

@Composable
@StorePreview
fun StoreMarketingScreenPreview() = StoreMarketingScreen(
    headlineLines = listOf("Your App", "in the Store"),
    subheadline = "Experience the best of Jetpack Compose",
    backgroundColor = Color(0xFF6200EE),
) {
    ScreenContentPlaceholder()
}
