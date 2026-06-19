@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.beryukhov.coffeegram.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.repository.CoffeeShop

private val DEFAULT_TAGS = listOf("espresso", "batch", "v60", "roaster")

internal class AdminConsoleState(
    private val repository: AdminRepository,
    private val scope: CoroutineScope,
    private val onAuthExpired: () -> Unit,
) {
    var shops by mutableStateOf<List<CoffeeShop>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var saving by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
    var selectedId by mutableStateOf<String?>(null)
        private set

    var name by mutableStateOf("")
    var description by mutableStateOf("")
    var coordinates by mutableStateOf("")
    val tags = mutableStateListOf<String>()
    var updatedAt by mutableStateOf<String?>(null)
        private set

    val coordinatesValid: Boolean
        get() = coordinates.isBlank() || parseCoordinates(coordinates) != null

    fun load() {
        scope.launch {
            repository.list().handle { shops = it }
            loading = false
        }
    }

    fun select(shop: CoffeeShop) {
        selectedId = shop.id
        name = shop.name
        description = shop.description
        coordinates = formatCoordinates(shop.latitude, shop.longitude)
        tags.clear()
        tags.addAll(shop.tags)
        updatedAt = shop.updatedAt?.toString()
        message = null
    }

    fun newShop() {
        selectedId = null
        name = ""
        description = ""
        coordinates = ""
        tags.clear()
        updatedAt = null
        message = null
    }

    fun addTag(raw: String) {
        val tag = raw.trim().lowercase()
        if (tag.isNotEmpty() && tag !in tags) tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun save() {
        val parsed = parseCoordinates(coordinates)
        if (parsed == null) {
            message = "Enter coordinates as \"latitude, longitude\"."
            return
        }
        if (name.isBlank()) {
            message = "Name can't be empty."
            return
        }
        val input = CoffeeShopInput(
            name = name.trim(),
            description = description.trim(),
            latitude = parsed.first,
            longitude = parsed.second,
            tags = tags.toList(),
        )
        saving = true
        scope.launch {
            val id = selectedId
            val result = if (id == null) repository.create(input) else repository.update(id, input)
            result.handle { saved ->
                message = "Saved"
                load()
                select(saved)
            }
            saving = false
        }
    }

    fun delete() {
        val id = selectedId ?: return
        saving = true
        scope.launch {
            repository.delete(id).handle {
                message = "Deleted"
                newShop()
                load()
            }
            saving = false
        }
    }

    private inline fun <T> Result<T>.handle(onOk: (T) -> Unit) {
        onSuccess(onOk)
        onFailure { error ->
            if (error is AdminAuthException) onAuthExpired() else message = error.message
        }
    }
}

@Composable
internal fun AdminConsole(repository: AdminRepository, onSignOut: () -> Unit) {
    val scope = rememberCoroutineScope()
    val state = remember(repository) { AdminConsoleState(repository, scope, onSignOut) }
    LaunchedEffect(repository) { state.load() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onSignOut)
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxSize()) {
                CollectionPane(state, modifier = Modifier.width(320.dp).fillMaxHeight())
                VerticalDivider()
                EditorPane(state, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun TopBar(onSignOut: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Coffeegram admin", style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onSignOut) { Text("Sign out") }
    }
}

@Composable
private fun CollectionPane(state: AdminConsoleState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My collection · ${state.shops.size}", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = state::newShop) { Text("+ New") }
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.shops, key = { it.id }) { shop ->
                    ShopRow(
                        shop = shop,
                        selected = shop.id == state.selectedId,
                        onClick = { state.select(shop) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ShopRow(shop: CoffeeShop, selected: Boolean, onClick: () -> Unit) {
    val background =
        if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
    ) {
        Text(shop.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            formatCoordinates(shop.latitude, shop.longitude),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (shop.tags.isNotEmpty()) {
            Text(
                shop.tags.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EditorPane(state: AdminConsoleState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            if (state.selectedId == null) "New shop" else "Edit shop",
            style = MaterialTheme.typography.headlineSmall,
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.description,
            onValueChange = { state.description = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.coordinates,
            onValueChange = { state.coordinates = it },
            label = { Text("Coordinates") },
            singleLine = true,
            isError = !state.coordinatesValid,
            supportingText = {
                val parsed = parseCoordinates(state.coordinates)
                when {
                    state.coordinates.isBlank() -> Text("Paste \"latitude, longitude\" from Google Maps.")
                    parsed != null -> Text("→ ${parsed.first}, ${parsed.second}")
                    else -> Text("Expected two numbers: latitude, longitude.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        TagEditor(state)
        if (state.updatedAt != null) {
            Text(
                "Updated ${state.updatedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = state::save, enabled = !state.saving) { Text("Save changes") }
            if (state.selectedId != null) {
                OutlinedButton(onClick = state::delete, enabled = !state.saving) { Text("Delete") }
            }
            state.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun TagEditor(state: AdminConsoleState) {
    var custom by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tags", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.tags.forEach { tag ->
                InputChip(
                    selected = true,
                    onClick = { state.removeTag(tag) },
                    label = { Text(tag) },
                    trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove $tag") },
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DEFAULT_TAGS.filter { it !in state.tags }.forEach { tag ->
                TextButton(onClick = { state.addTag(tag) }) { Text("+ $tag") }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = custom,
                onValueChange = { custom = it },
                label = { Text("Custom tag") },
                singleLine = true,
                modifier = Modifier.width(220.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = {
                    state.addTag(custom)
                    custom = ""
                },
                enabled = custom.isNotBlank(),
            ) { Text("Add") }
        }
    }
}
