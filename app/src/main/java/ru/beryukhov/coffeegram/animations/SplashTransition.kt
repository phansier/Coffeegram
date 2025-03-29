package ru.beryukhov.coffeegram.animations

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
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

enum class SplashState(val transition: SplashTransition) {
    Shown(
        SplashTransition(
            splashAlpha = 1f,
            contentAlpha = 0f,
            contentTopPadding = 200.dp
        )
    ),
    Completed(
        SplashTransition(
            splashAlpha = 0f,
            contentAlpha = 1f,
            contentTopPadding = 0.dp
        )
    )
}

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
    modifier: Modifier = Modifier,
    doAnimation: Boolean = true,
    onAnimationEnded: () -> Unit = {},
) {
    if (doAnimation) {
        val transition = newSplashTransition()
        Box {
            StartPage(
                Modifier.alpha(transition.splashAlpha),
            )
            EndPage(
                Modifier.alpha(transition.contentAlpha),
                transition.contentTopPadding,
            )
        }
        if (transition.contentAlpha == SplashState.Completed.transition.contentAlpha) {
            onAnimationEnded()
        }
    } else {
        EndPage(
            Modifier.alpha(SplashState.Completed.transition.contentAlpha),
            SplashState.Completed.transition.contentTopPadding,
        )
    }
}

@Composable
@Preview
internal fun TransitionSlotPreview() {
    TransitionSlot(
        StartPage = { modifier ->
            SplashContent(modifier = modifier)
        },
        EndPage = { modifier, topPadding ->
            InnerContent(topPadding, modifier)
        },
    )
}

@Composable
@Preview
internal fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.Green)
            .fillMaxSize()

    )
}

@Composable
@Preview
private fun InnerContentPreview(modifier: Modifier = Modifier) = InnerContent(modifier = Modifier, topPadding = 100.dp)

@Composable
fun InnerContent(topPadding: Dp, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = topPadding)
                .align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = Modifier
                .background(Color.Red)
                .fillMaxSize()
        )
    }
}

@Composable
fun newSplashTransition(): SplashTransition {
    val visibleState = remember { MutableTransitionState(SplashState.Shown) }
    visibleState.targetState = SplashState.Completed
    val transition = rememberTransition(visibleState, "splashTransition")
    val splashAlpha by transition.animateFloat(
        transitionSpec = { tween(3000) },
        label = "splashTransitionAlpha"
    ) { it.transition.splashAlpha }
    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(3000) },
        label = "contentTransitionAlpha"
    ) { it.transition.contentAlpha }
    val contentTopPadding by transition.animateDp(
        transitionSpec = { tween(2000) },
        label = "contentTransitionPadding"
    ) { it.transition.contentTopPadding }

    return SplashTransition(splashAlpha, contentAlpha, contentTopPadding)
}
