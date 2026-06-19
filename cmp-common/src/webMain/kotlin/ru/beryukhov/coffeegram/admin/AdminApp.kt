package ru.beryukhov.coffeegram.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

internal fun isAdminRoute(): Boolean = locationSearch().contains("admin", ignoreCase = true)

@Composable
fun AdminApp() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Coffeegram admin — coming soon",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
