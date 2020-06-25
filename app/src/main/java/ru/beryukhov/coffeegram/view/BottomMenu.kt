package ru.beryukhov.coffeegram.view

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.DateRange
import androidx.ui.material.icons.filled.Info
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomMenu(selectedItemFlow: MutableStateFlow<Int>) {
    val items = listOf("Calendar" to Icons.Filled.DateRange, "Info" to Icons.Filled.Info)
    val selectedItem by selectedItemFlow.collectAsState()
    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(item.second) },
                text = { Text(item.first) },
                selected = selectedItem == index,
                onSelected = { selectedItemFlow.value = index }
            )
        }
    }
}
