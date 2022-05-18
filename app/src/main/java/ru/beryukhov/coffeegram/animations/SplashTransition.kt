package ru.beryukhov.coffeegram.animations

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class SplashState { Shown, Completed }

data class SplashTransition(
    val splashAlpha: Float,
    val contentAlpha: Float,
    val contentTopPadding: Dp
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun TransitionSlot(
    StartPage: @Composable (modifier: Modifier) -> Unit,
    EndPage: @Composable (modifier: Modifier, topPadding: Dp) -> Unit,
) {
    val transition = newSplashTransition()
    Box {
        StartPage(
            modifier = Modifier.alpha(transition.splashAlpha),
        )
        EndPage(
            modifier = Modifier.alpha(transition.contentAlpha),
            topPadding = transition.contentTopPadding,
        )
    }
}

@Composable
@Preview
fun TransitionSlotPreview() {
    TransitionSlot(
        StartPage = { modifier ->
            SplashContent(modifier = modifier)
        },
        EndPage = { modifier, topPadding ->
            InnerContent(modifier, topPadding)
        }
    )
}

@Composable
@Preview
fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.Green)
            .fillMaxSize()

    )
}

@Composable
@Preview
fun InnerContentPreview() = InnerContent(modifier = Modifier, topPadding = 100.dp)

@Composable
fun InnerContent(modifier: Modifier, topPadding: Dp) {

    Column(
        modifier = modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Spacer(
            modifier = modifier
                .padding(top = topPadding)
                .align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = modifier
                .background(Color.Red)
                .fillMaxSize()
        )
    }
}

@Composable
fun newSplashTransition(): SplashTransition {
    val visibleState = remember { MutableTransitionState(SplashState.Shown) }
    visibleState.targetState = SplashState.Completed
    val transition = updateTransition(visibleState, label = "splashTransition")
    val splashAlpha by transition.animateFloat(
        transitionSpec = { tween(3000) }, label = "splashTransitionAlpha"
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
        transitionSpec = { tween(2000) }, label = "contentTransitionPadding"
    ) { splashState ->
        when (splashState) {
            SplashState.Shown -> 200.dp
            SplashState.Completed -> 0.dp
        }
    }
    return SplashTransition(splashAlpha, contentAlpha, contentTopPadding)
}
