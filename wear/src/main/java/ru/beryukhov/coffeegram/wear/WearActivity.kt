package ru.beryukhov.coffeegram.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.wear.compose.material3.AppScaffold
import ru.beryukhov.coffeegram.data.DayCoffee

class WearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coffeeRepository.loadFromDataLayer()
        setContent {
            CoffeegramTheme {
                AppScaffold {
                    TodayRoute(coffeeRepository)
                }
            }
        }
    }
}

@Composable
private fun TodayRoute(repository: WearCoffeeRepository) {
    val dayCoffee by repository.todayCoffee.collectAsState(initial = DayCoffee())
    TodayScreen(
        dayCoffee = dayCoffee,
        onIncrement = { type -> repository.change(type, +1) },
        onDecrement = { type -> repository.change(type, -1) },
    )
}
