import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes.Cappuccino
import ru.beryukhov.coffeegram.data.Picture
import ru.beryukhov.coffeegram.data.PrintableText
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.coffeegram.view.SampleTable
import ru.beryukhov.date_time_utils.YearMonth

/**
 * - [Compose Preview screenshot do not work with compose multiplatform resources](https://issuetracker.google.com/issues/402137754)
 * - [Screenshot Preview Testing: Unable to use resource assets from another module](https://issuetracker.google.com/issues/393685881)
 */
@PreviewTest
@Preview
@Preview(name = "Large preview", widthDp = 200)
@Composable
private fun Preview() = PreviewTheme {
    var count by remember { mutableIntStateOf(5) }
    CoffeeTypeItem(
        coffeeType = Cappuccino,
        count = count,
        onIncrement = { count++ },
        onDecrement = { count-- }
    )
}

@PreviewTest
@Preview(showBackground = true)
@Composable
internal fun TablePreview() = PreviewTheme {
    SampleTable()
}
