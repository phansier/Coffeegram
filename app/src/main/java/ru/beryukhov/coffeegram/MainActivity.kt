package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.setContent
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.Cappucino
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.TablePage
import ru.beryukhov.coffeegram.view.BottomMenu

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }
}

val selectedItemFlow = MutableStateFlow(0)
val yearMonthFlow = MutableStateFlow(YearMonth.now())

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoffeegramTheme {
        Scaffold() {
            Column {
                PagesContent(selectedItemFlow)
                BottomMenu(selectedItemFlow)
            }
        }
    }
}

@Composable
fun PagesContent(selectedItemFlow: MutableStateFlow<Int>) {
    val selectedItem by selectedItemFlow.collectAsState()

    when (selectedItem) {
        0 -> TablePage(yearMonthFlow)//todo add real flow as a second parameter
        1 -> CoffeeListPage(MutableStateFlow(DayCoffee(mapOf(Cappucino to 5))))//todo replace by real flow
    }
}

