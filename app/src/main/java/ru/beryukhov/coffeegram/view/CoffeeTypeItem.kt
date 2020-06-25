package ru.beryukhov.coffeegram.view

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.*
import androidx.ui.material.TextButton
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import ru.beryukhov.coffeegram.app_ui.typography
import ru.beryukhov.coffeegram.data.Cappucino
import ru.beryukhov.coffeegram.data.CoffeeType

@Composable
fun CoffeeTypeItem(type: CoffeeType, count: Int/*Todo make it mutable*/) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(type.icon, modifier = Modifier.preferredSize(48.dp))
        /*Image(
            imageResource(type.image), modifier = Modifier
                .preferredHeightIn(maxHeight = 48.dp)
                .preferredWidthIn(maxWidth = 48.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(24.dp))
                .gravity(Alignment.CenterVertically),
            contentScale = ContentScale.Crop
        )*/
        Spacer(Modifier.preferredWidth(16.dp))
        Text(
            type.name, style = typography.body1,
            modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
        )
        Row(modifier = Modifier.gravity(Alignment.CenterVertically)) {
            val count = state { count }
            Spacer(Modifier.preferredWidth(16.dp))
            val textButtonModifier = Modifier.gravity(Alignment.CenterVertically)
                .preferredSizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(
                onClick = { count.value-- },
                padding = InnerPadding(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "${count.value}", style = typography.body2,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { count.value++ },
                padding = InnerPadding(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}

@Preview
@Composable
private fun preview() {
    CoffeeTypeItem(
        Cappucino, 4
    )
}