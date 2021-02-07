package ru.beryukhov.coffeegram.animations

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

enum class SplashState { Shown, Completed }

val splashAlphaKey = FloatPropKey(label = "splashAlphaKey")
val contentAlphaKey = FloatPropKey(label = "contentAlphaKey")
val contentTopPaddingKey = DpPropKey(label = "contentTopPaddingKey")

//should use updateTransition or rememberInfiniteTransition
private val splashTransitionDefinition = transitionDefinition<SplashState> {
    state(SplashState.Shown) {
        this[splashAlphaKey] = 1f
        this[contentAlphaKey] = 0f
        this[contentTopPaddingKey] = 100.dp
    }
    state(SplashState.Completed) {
        this[splashAlphaKey] = 0f
        this[contentAlphaKey] = 1f
        this[contentTopPaddingKey] = 0.dp
    }
    transition {
        splashAlphaKey using tween(
            durationMillis = 1000
        )
        contentAlphaKey using tween(
            durationMillis = 3000
        )
        contentTopPaddingKey using spring(
            stiffness = Spring.StiffnessLow
        )
    }
}

@Composable
fun splashTransition(splashState: SplashState) = transition(splashTransitionDefinition, splashState)

