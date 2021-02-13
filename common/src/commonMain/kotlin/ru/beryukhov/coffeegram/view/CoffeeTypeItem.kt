package ru.beryukhov.coffeegram.view

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.LocalDate
import ru.beryukhov.coffeegram.data.Cappucino
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.now


@Composable
fun CoffeeTypeItem(
    localDate: LocalDate,
    coffeeType: CoffeeType,
    count: Int,
    daysCoffeesStore: DaysCoffeesStore
) {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        Image(coffeeType, modifier = Modifier
            .size(48.dp)
            .align(Alignment.CenterVertically))
        Spacer(Modifier.width(16.dp))
        Text(
            coffeeType.name, style = typography.body1,
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
        )
        Row(modifier = Modifier.align(Alignment.CenterVertically)) {
            Spacer(Modifier.width(16.dp))
            val textButtonModifier = Modifier.align(Alignment.CenterVertically)
                .sizeIn(
                    maxWidth = 32.dp,
                    maxHeight = 32.dp,
                    minWidth = 0.dp,
                    minHeight = 0.dp
                )
            TextButton(
                onClick = { daysCoffeesStore.newIntent(DaysCoffeesIntent.MinusCoffee(localDate, coffeeType)) },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "$count", style = typography.body2,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = { daysCoffeesStore.newIntent(DaysCoffeesIntent.PlusCoffee(localDate, coffeeType)) },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}

//@Preview
@Composable
fun preview() {
    CoffeeTypeItem(
        now(), Cappucino, 5, DaysCoffeesStore()
    )
}