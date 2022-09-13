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
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.app_ui.AppTypography
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeType.Cappuccino
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.pages.CoffeeListViewModel
import ru.beryukhov.coffeegram.pages.CoffeeListViewModelStub
import ru.beryukhov.coffeegram.pages.localDateStub

@Composable
fun CoffeeTypeItem(
    localDate: LocalDate,
    coffeeType: CoffeeType,
    count: Int,
    coffeeListViewModel: CoffeeListViewModel,
    modifier: Modifier = Modifier,
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
            stringResource(coffeeType.nameId), style = AppTypography.bodyMedium,
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
            val isReduceCountAllowed = count > 0
            TextButton(
                enabled = isReduceCountAllowed,
                onClick = {
                    coffeeListViewModel.newIntent(
                        DaysCoffeesIntent.MinusCoffee(
                            localDate,
                            coffeeType
                        )
                    )
                },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("-")
            }
            Text(
                "$count", style = AppTypography.bodySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = {
                    coffeeListViewModel.newIntent(
                        DaysCoffeesIntent.PlusCoffee(
                            localDate,
                            coffeeType
                        )
                    )
                },
                contentPadding = PaddingValues(0.dp),
                modifier = textButtonModifier
            ) {
                Text("+")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CoffeeTypeItem(
        localDateStub, Cappuccino, 5, CoffeeListViewModelStub
    )
}
