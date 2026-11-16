package ru.beryukhov.coffeegram.wear

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.RevealDirection
import androidx.wear.compose.material.RevealState
import androidx.wear.compose.material.RevealValue
import androidx.wear.compose.material.SwipeToRevealChip
import androidx.wear.compose.material.SwipeToRevealDefaults
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberRevealState
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.printableText

@Composable
fun CoffeeItem(
    c: CoffeeType,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDecrement = count > 0
    key(canDecrement) {
        SwipeableCoffeeChip(
            c = c,
            count = count,
            canDecrement = canDecrement,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun SwipeableCoffeeChip(
    c: CoffeeType,
    count: Int,
    canDecrement: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val revealState = rememberRevealState(anchors = swipeAnchors(canDecrement))
    val accessibilityActions = accessibilityActions(canDecrement, onIncrement, onDecrement)
    val revealScope = rememberCoroutineScope()
    val revealsIncrement by remember { derivedStateOf { revealState.offset > 0f } }
    val applyRevealedAction = if (revealsIncrement) onIncrement else onDecrement
    CoverAfterFullSwipe(revealState)
    SwipeToRevealChip(
        revealState = revealState,
        modifier = modifier.fillMaxWidth(),
        onFullSwipe = applyRevealedAction,
        colors = revealedActionColors(revealsIncrement),
        primaryAction = {
            SwipeToRevealPrimaryAction(
                revealState = revealState,
                onClick = {
                    applyRevealedAction()
                    revealScope.launch { revealState.animateTo(RevealValue.Covered) }
                },
                icon = { Text(text = stringResource(if (revealsIncrement) R.string.plus_one else R.string.minus_one)) },
                label = {},
            )
        },
    ) {
        CoffeeChip(
            c = c,
            count = count,
            onIncrement = onIncrement,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { customActions = accessibilityActions },
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
private fun swipeAnchors(canDecrement: Boolean): Map<RevealValue, Float> {
    val bothSides = SwipeToRevealDefaults.createRevealAnchors(revealDirection = RevealDirection.Both)
    return if (canDecrement) bothSides else bothSides.filterValues { it <= 0f }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun revealedActionColors(revealsIncrement: Boolean) =
    if (revealsIncrement) {
        SwipeToRevealDefaults.actionColors(
            primaryActionBackgroundColor = MaterialTheme.colors.secondary,
            primaryActionContentColor = MaterialTheme.colors.onSecondary,
        )
    } else {
        SwipeToRevealDefaults.actionColors()
    }

@Composable
private fun accessibilityActions(
    canDecrement: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
): List<CustomAccessibilityAction> {
    val addOne = CustomAccessibilityAction(stringResource(R.string.add_one)) { onIncrement(); true }
    val removeOne = CustomAccessibilityAction(stringResource(R.string.remove_one)) { onDecrement(); true }
    return if (canDecrement) listOf(addOne, removeOne) else listOf(addOne)
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun CoverAfterFullSwipe(revealState: RevealState) {
    LaunchedEffect(revealState.currentValue) {
        if (revealState.currentValue == RevealValue.RightRevealed || revealState.currentValue == RevealValue.LeftRevealed) {
            revealState.animateTo(RevealValue.Covered)
        }
    }
}

@Composable
private fun CoffeeChip(
    c: CoffeeType,
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Chip(
        modifier = modifier,
        icon = {
            c.icon(
                modifier = Modifier
                    .size(ChipDefaults.IconSize)
                    .alpha(LocalContentAlpha.current)
            )
        },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = printableText(c.localizedName) + " - " + count
            )
        },
        onClick = onIncrement,
    )
}
