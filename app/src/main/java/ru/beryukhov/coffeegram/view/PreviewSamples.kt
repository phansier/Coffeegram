package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.app_ui.typography
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeType.Cappuccino

@Preview
// @Preview(name = "Large preview", widthDp = 200) //causes compile error but works in preview
@Composable
private fun Preview() {
    CoffeeTypeItemRaw(
        Cappuccino, 5
    )
}

/**
 * A simplified version of [CoffeeTypeItem] to be displayed with Preview
 */
@Composable
private fun CoffeeTypeItemRaw(
    coffeeType: CoffeeType,
    count: Int
) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = coffeeType.iconId),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            stringResource(coffeeType.nameId), style = typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
        Row(modifier = Modifier.align(Alignment.CenterVertically)) {
            Spacer(Modifier.width(16.dp))
            val textButtonModifier = Modifier
                .align(Alignment.CenterVertically)
                .sizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(
                onClick = { },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "$count", style = typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}
