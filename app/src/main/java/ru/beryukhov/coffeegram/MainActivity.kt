package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.setContent
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.*
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

//val selectedItemFlow = MutableStateFlow(0)
val yearMonthFlow = MutableStateFlow(YearMonth.now())
val dateFlow = MutableStateFlow(-1)
val daysCoffeesFlow: DaysCoffeesFlow = MutableStateFlow(mapOf())

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoffeegramTheme {
        Scaffold() {
            Column {
                PagesContent(dateFlow)
                //BottomMenu(selectedItemFlow)
            }
        }
    }
}

@Composable
fun PagesContent(dateFlow: MutableStateFlow<Int>) {
    val date by dateFlow.collectAsState()

    when (date) {
        -1 -> TablePage(yearMonthFlow, daysCoffeesFlow, dateFlow)
        else -> CoffeeListPage(
            DayCoffeeFlow(
                daysCoffeesFlow,
                LocalDate.of(yearMonthFlow.value.year, yearMonthFlow.value.month, date)
            ),
            dateFlow
        )
    }
}
