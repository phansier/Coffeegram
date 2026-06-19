@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.beryukhov.coffeegram.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.repository.CoffeeShop

private val DEFAULT_TAGS = listOf("espresso", "batch", "v60", "roaster")
private val WIDE_THRESHOLD = 600.dp

@Composable
internal fun AdminConsole(repository: AdminRepository, onSignOut: () -> Unit) {
    val scope = rememberCoroutineScope()
    val store = remember(repository) { AdminConsoleStore(repository, scope, onSignOut) }
    LaunchedEffect(repository) { store.load() }
    AdminConsoleContent(state = store, callbacks = store, onSignOut = onSignOut)
}

@Composable
private fun AdminConsoleContent(
    state: AdminConsoleState,
    callbacks: AdminConsoleCallbacks,
    onSignOut: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onSignOut)
            HorizontalDivider()
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                if (maxWidth >= WIDE_THRESHOLD) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        CollectionPane(state, callbacks, modifier = Modifier.width(320.dp).fillMaxHeight())
                        VerticalDivider()
                        EditorPane(state, callbacks, showBack = false, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                } else if (state.editing) {
                    EditorPane(state, callbacks, showBack = true, modifier = Modifier.fillMaxSize())
                } else {
                    CollectionPane(state, callbacks, modifier = Modifier.fillMaxSize())
                }
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
private fun CollectionPane(
    state: AdminConsoleState,
    callbacks: AdminConsoleCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My collection · ${state.shops.size}", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = callbacks::newShop) { Text("+ New") }
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.shops, key = { it.id }) { shop ->
                    ShopRow(
                        shop = shop,
                        selected = shop.id == state.selectedId,
                        onClick = { callbacks.select(shop) },
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
            .background(background)
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
private fun EditorPane(
    state: AdminConsoleState,
    callbacks: AdminConsoleCallbacks,
    showBack: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showBack) {
                IconButton(onClick = callbacks::closeEditor) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to collection")
                }
            }
            Text(
                if (state.selectedId == null) "New shop" else "Edit shop",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        OutlinedTextField(
            value = state.name,
            onValueChange = callbacks::onNameChange,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.description,
            onValueChange = callbacks::onDescriptionChange,
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.coordinates,
            onValueChange = callbacks::onCoordinatesChange,
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
        TagEditor(state, callbacks)
        if (state.updatedAt != null) {
            Text(
                "Updated ${state.updatedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = callbacks::save, enabled = !state.saving) { Text("Save changes") }
            if (state.selectedId != null) {
                OutlinedButton(onClick = callbacks::delete, enabled = !state.saving) { Text("Delete") }
            }
            state.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun TagEditor(state: AdminConsoleState, callbacks: AdminConsoleCallbacks) {
    var custom by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tags", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.tags.forEach { tag ->
                InputChip(
                    selected = true,
                    onClick = { callbacks.removeTag(tag) },
                    label = { Text(tag) },
                    trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove $tag") },
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DEFAULT_TAGS.filter { it !in state.tags }.forEach { tag ->
                TextButton(onClick = { callbacks.addTag(tag) }) { Text("+ $tag") }
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
            TextButton(
                onClick = {
                    callbacks.addTag(custom)
                    custom = ""
                },
                enabled = custom.isNotBlank(),
            ) { Text("Add") }
        }
    }
}

private val sampleShops = listOf(
    CoffeeShop(
        name = "Coltivare",
        description = "Tiny roastery counter, rotating single-origin filter.",
        latitude = 35.17390,
        longitude = 33.36180,
        id = "9f3a1c",
        tags = listOf("v60", "roaster", "batch"),
    ),
    CoffeeShop(
        name = "Hoi Polloi",
        description = "Limassol seafront, batch brew that holds up in the heat.",
        latitude = 34.67860,
        longitude = 33.04130,
        id = "c41d8a",
        tags = listOf("batch", "espresso"),
    ),
)

private val previewState = object : AdminConsoleState {
    override val shops = sampleShops
    override val loading = false
    override val saving = false
    override val message: String? = null
    override val selectedId = sampleShops.first().id
    override val editing = true
    override val name = "Coltivare"
    override val description = "Tiny roastery counter, rotating single-origin filter."
    override val coordinates = "35.1739, 33.3618"
    override val tags = listOf("v60", "roaster")
    override val updatedAt = "2026-06-18T14:22:00Z"
    override val coordinatesValid = true
}

private val emptyCallbacks = object : AdminConsoleCallbacks {
    override fun load() = Unit
    override fun select(shop: CoffeeShop) = Unit
    override fun newShop() = Unit
    override fun closeEditor() = Unit
    override fun onNameChange(value: String) = Unit
    override fun onDescriptionChange(value: String) = Unit
    override fun onCoordinatesChange(value: String) = Unit
    override fun addTag(raw: String) = Unit
    override fun removeTag(tag: String) = Unit
    override fun save() = Unit
    override fun delete() = Unit
}

@Preview
@Composable
private fun CollectionPanePreview() {
    MaterialTheme {
        Surface { CollectionPane(previewState, emptyCallbacks) }
    }
}

@Preview
@Composable
private fun EditorPanePreview() {
    MaterialTheme {
        Surface { EditorPane(previewState, emptyCallbacks, showBack = false) }
    }
}

@Preview
@Composable
private fun ShopRowPreview() {
    MaterialTheme {
        Surface { ShopRow(shop = sampleShops.first(), selected = true, onClick = {}) }
    }
}

@Preview
@Composable
private fun TagEditorPreview() {
    MaterialTheme {
        Surface { TagEditor(previewState, emptyCallbacks) }
    }
}

@Preview
@Composable
private fun AdminConsoleContentPreview() {
    MaterialTheme {
        AdminConsoleContent(state = previewState, callbacks = emptyCallbacks, onSignOut = {})
    }
}
