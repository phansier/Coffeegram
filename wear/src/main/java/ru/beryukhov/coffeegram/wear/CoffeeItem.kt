package ru.beryukhov.coffeegram.wear

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RevealDirection
import androidx.wear.compose.material3.RevealState
import androidx.wear.compose.material3.RevealValue
import androidx.wear.compose.material3.RevealValue.Companion.LeftRevealed
import androidx.wear.compose.material3.RevealValue.Companion.RightRevealed
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.SwipeToReveal
import androidx.wear.compose.material3.SwipeToRevealScope
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.rememberRevealState
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.printableText

@Composable
fun CoffeeItem(
    c: CoffeeType,
    count: Int,
    isListScrolling: Boolean,
    transformation: SurfaceTransformation,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDecrement = count > 0
    val revealState = rememberRevealState()
    val revealsIncrement by remember { derivedStateOf { revealState.offset > 0f } }
    val accessibilityActions = accessibilityActions(canDecrement, onIncrement, onDecrement)
    val applyRevealedAction = { if (revealState.isIncrementSide()) onIncrement() else onDecrement() }
    CoverWhenListScrolls(revealState, isListScrolling)
    CoverAfterAction(revealState)
    CoverWhenDecrementUnavailable(revealState, canDecrement)
    SwipeToReveal(
        revealState = revealState,
        revealDirection = RevealDirection.Bidirectional,
        onSwipePrimaryAction = applyRevealedAction,
        modifier = modifier,
        primaryAction = {
            when {
                revealsIncrement -> IncrementAction(onClick = applyRevealedAction)
                canDecrement -> DecrementAction(onClick = applyRevealedAction)
            }
        },
    ) {
        CoffeeButton(
            c = c,
            count = count,
            onIncrement = onIncrement,
            transformation = transformation,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { customActions = accessibilityActions },
        )
    }
}

@Composable
private fun SwipeToRevealScope.IncrementAction(onClick: () -> Unit) {
    PrimaryActionButton(
        onClick = onClick,
        icon = { Text(text = stringResource(R.string.plus_one)) },
        text = { Text(text = stringResource(R.string.add_one)) },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
    )
}

@Composable
private fun SwipeToRevealScope.DecrementAction(onClick: () -> Unit) {
    PrimaryActionButton(
        onClick = onClick,
        icon = { Text(text = stringResource(R.string.minus_one)) },
        text = { Text(text = stringResource(R.string.remove_one)) },
    )
}

private fun RevealState.isIncrementSide(): Boolean =
    currentValue == RevealValue.LeftRevealing ||
        currentValue == LeftRevealed ||
        targetValue == RevealValue.LeftRevealing ||
        targetValue == LeftRevealed

private fun RevealValue.isDecrementSide(): Boolean =
    this == RevealValue.RightRevealing || this == RightRevealed

@Composable
private fun CoverWhenListScrolls(revealState: RevealState, isListScrolling: Boolean) {
    LaunchedEffect(isListScrolling) {
        if (isListScrolling && revealState.currentValue != RevealValue.Covered) {
            revealState.animateTo(RevealValue.Covered)
        }
    }
}

@Composable
private fun CoverAfterAction(revealState: RevealState) {
    LaunchedEffect(revealState.currentValue) {
        if (revealState.currentValue == LeftRevealed || revealState.currentValue == RightRevealed) {
            revealState.animateTo(RevealValue.Covered)
        }
    }
}

@Composable
private fun CoverWhenDecrementUnavailable(revealState: RevealState, canDecrement: Boolean) {
    LaunchedEffect(revealState.currentValue, canDecrement) {
        if (!canDecrement && revealState.currentValue.isDecrementSide()) {
            revealState.animateTo(RevealValue.Covered)
        }
    }
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

@Composable
private fun CoffeeButton(
    c: CoffeeType,
    count: Int,
    onIncrement: () -> Unit,
    transformation: SurfaceTransformation,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onIncrement,
        modifier = modifier,
        transformation = transformation,
        icon = { c.icon(modifier = Modifier.size(ButtonDefaults.LargeIconSize)) },
        label = {
            Text(
                text = printableText(c.localizedName),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryLabel = { Text(text = count.toString()) },
    )
}
