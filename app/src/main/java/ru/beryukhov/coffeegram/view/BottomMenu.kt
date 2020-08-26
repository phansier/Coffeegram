package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomMenu(selectedItemFlow: MutableStateFlow<Int>) {
    val items = listOf("Calendar" to Icons.Filled.DateRange, "Info" to Icons.Filled.Info)
    val selectedItem:Int by selectedItemFlow.collectAsState()
    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(item.second) },
                label = { Text(item.first) },
                selected = selectedItem == index,
                onSelect = { selectedItemFlow.value = index }
            )
        }
    }
}
