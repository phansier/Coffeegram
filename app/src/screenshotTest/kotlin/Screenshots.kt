import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.CoffeeType.Cappuccino
import ru.beryukhov.coffeegram.view.CoffeeTypeItem
import ru.beryukhov.coffeegram.view.SampleTable

@Preview
@Preview(name = "Large preview", widthDp = 200)
@Composable
private fun Preview() {
    CoffeeTypeItem(Cappuccino, 5, {}, {})
}

@Preview(showBackground = true)
@Composable
internal fun TablePreview() {
    CoffeegramTheme {
        SampleTable()
    }
}
