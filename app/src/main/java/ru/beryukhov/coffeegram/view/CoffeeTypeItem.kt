package ru.beryukhov.coffeegram.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cappuccino
import ru.beryukhov.coffeegram.data.printableText

@Composable
fun CoffeeTypeItem(
    coffeeType: CoffeeType,
    count: Int,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(16.dp)
    ) {
        coffeeType.icon(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            printableText(coffeeType.localizedName),
            style = typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .testTag("CoffeeName")
        )
        Row(modifier = Modifier.align(Alignment.CenterVertically).testTag("CoffeeNam1e")) {
            Spacer(Modifier.width(16.dp))
            val textButtonModifier = Modifier
                .align(Alignment.CenterVertically)
                .sizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            val isReduceCountAllowed = count > 0
            TextButton(
                enabled = isReduceCountAllowed,
                onClick = onMinusClick,
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            AnimatedCounter(count)
            TextButton(
                onClick = onPlusClick,
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}

private const val ZERO_WIDTH_CHAR = '\u200B'

@Composable
internal fun RowScope.AnimatedCounter(count: Int) {
    count.toString()
        // For correct transitions when the digit count changes. Up to 999
        .padStart(3, ZERO_WIDTH_CHAR)
        .map { c -> Pair(c, count) }
        .forEach { digit ->
            AnimatedContent(
                targetState = digit,
                transitionSpec = {
                    if (targetState.first == initialState.first) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else if (targetState.second > initialState.second) {
                        slideInVertically { -it } togetherWith slideOutVertically { it }
                    } else {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    }
                },
                label = "CounterAnimation",
                modifier = Modifier.align(Alignment.CenterVertically)
            ) { (digit, _) ->
                Text(
                    "$digit",
                    style = typography.bodySmall,
                )
            }
        }
}

// Background for dark mode
@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun Preview() {
    var count by remember { mutableIntStateOf(5) }
    CoffeeTypeItem(
        coffeeType = Cappuccino,
        count = count,
        onPlusClick = { count++ },
        onMinusClick = { count-- }
    )
}
