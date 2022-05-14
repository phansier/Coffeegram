package ru.beryukhov.coffeegram.view

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    val selectedItem: Int by selectedItemFlow.collectAsState()
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.second, contentDescription = "") },
                label = { Text(item.first) },
                selected = selectedItem == index,
                onClick = { selectedItemFlow.value = index }
            )
        }
    }
}
