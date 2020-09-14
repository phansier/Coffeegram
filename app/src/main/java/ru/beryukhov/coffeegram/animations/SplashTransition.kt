package ru.beryukhov.coffeegram.animations

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.dp

enum class SplashState { Shown, Completed }

val splashAlphaKey = FloatPropKey()
val contentAlphaKey = FloatPropKey()
val contentTopPaddingKey = DpPropKey()

val splashTransitionDefinition = transitionDefinition<SplashState> {
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
