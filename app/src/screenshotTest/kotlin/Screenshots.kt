import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.coffeegram.data.PrintableText
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.YearMonth

/**
 * - [Compose Preview screenshot do not work with compose multiplatform resources](https://issuetracker.google.com/issues/402137754)
 * - [Screenshot Preview Testing: Unable to use resource assets from another module](https://issuetracker.google.com/issues/393685881)
 */
@Preview
@Preview(name = "Large preview", widthDp = 200)
@Composable
private fun Preview() {
    CoffeeTypeItem(object : CoffeeType {
        override val localizedName: PrintableText = PrintableText("Coffeegram")
        override val icon: Picture = Picture.EMPTY
        override val dbKey: String = "Cappuccino"
    }, 5, {}, {})
}

@Preview(showBackground = true)
@Composable
internal fun TablePreview() {
    MonthTable(
        yearMonth = YearMonth(2020, Month(7)),
        today = LocalDate(2020, 7, 14),
        filledDayItemsMap = persistentMapOf(),
        onClick = {},
        modifier = Modifier,
    )
}
