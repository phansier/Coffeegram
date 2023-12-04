package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.brown500

@Composable
fun LandingPage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(brown500),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo_splash), contentDescription = "")
    }
}
