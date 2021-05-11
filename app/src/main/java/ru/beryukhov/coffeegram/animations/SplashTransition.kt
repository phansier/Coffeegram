package ru.beryukhov.coffeegram.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.PagesContent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.pages.LandingPage

enum class SplashState { Shown, Completed }

data class SplashTransition(
    val splashAlpha: Float,
    val contentAlpha: Float,
    val contentTopPadding: Dp
)

@Composable
fun newSplashTransition(): SplashTransition {
    val visibleState = remember { MutableTransitionState(SplashState.Shown) }
    visibleState.targetState = SplashState.Completed
    val transition = updateTransition(visibleState, label = "splashTransition")
    val splashAlpha by transition.animateFloat(
        transitionSpec = { tween(1000) }, label = "splashTransitionAlpha"
    ) { splashState ->
        when (splashState) {
            SplashState.Shown -> 1f
            SplashState.Completed -> 0f
        }
    }
    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(3000) }, label = "contentTransitionAlpha"
    ) { splashState ->
        when (splashState) {
            SplashState.Shown -> 0f
            SplashState.Completed -> 1f
        }
    }
    val contentTopPadding by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = "contentTransitionPadding"
    ) { splashState ->
        when (splashState) {
            SplashState.Shown -> 100.dp
            SplashState.Completed -> 0.dp
        }
    }
    return SplashTransition(splashAlpha, contentAlpha, contentTopPadding)
}


