package ru.beryukhov.coffeegram.view

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.layout.*
import androidx.ui.material.TextButton
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.app_ui.typography
import ru.beryukhov.coffeegram.data.Cappucino
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore


@Composable
fun CoffeeTypeItem(localDate: LocalDate, coffeeType: CoffeeType, count:Int, daysCoffeesStore: DaysCoffeesStore) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            vectorResource(id = coffeeType.iconId), modifier = Modifier
                .preferredSize(48.dp)
                .gravity(Alignment.CenterVertically)
        )
        Spacer(Modifier.preferredWidth(16.dp))
        Text(
            coffeeType.name, style = typography.body1,
            modifier = Modifier.gravity(Alignment.CenterVertically).weight(1f)
        )
        Row(modifier = Modifier.gravity(Alignment.CenterVertically)) {
            Spacer(Modifier.preferredWidth(16.dp))
            val textButtonModifier = Modifier.gravity(Alignment.CenterVertically)
                .preferredSizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(
                onClick = { daysCoffeesStore.newIntent(DaysCoffeesIntent.MinusCoffee(localDate, coffeeType)) },
                padding = InnerPadding(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "${count}", style = typography.body2,
                modifier = Modifier.gravity(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { daysCoffeesStore.newIntent(DaysCoffeesIntent.PlusCoffee(localDate, coffeeType)) },
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
        LocalDate.now(),Cappucino, 5, DaysCoffeesStore()
    )
}