package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import kotlinx.coroutines.delay
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.brown500

private const val SplashWaitTime: Long = 2000


@Composable
fun LandingPage(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
    Box(
        modifier = modifier.fillMaxSize().background(brown500),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(Any()) {
            delay(SplashWaitTime)
            onTimeout()
        }
        Image(bitmap = imageResource(id = R.drawable.logo_splash), contentDescription = "")
    }
}
