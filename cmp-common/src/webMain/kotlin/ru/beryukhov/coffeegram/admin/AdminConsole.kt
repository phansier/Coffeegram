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

internal data class EditorForm(
    val isNew: Boolean,
    val name: String,
    val description: String,
    val coordinates: String,
    val coordinatesValid: Boolean,
    val tags: List<String>,
    val updatedAt: String?,
    val saving: Boolean,
    val message: String?,
)

@Composable
internal fun AdminConsole(repository: AdminRepository, onSignOut: () -> Unit) {
    val scope = rememberCoroutineScope()
    val state = remember(repository) { AdminConsoleState(repository, scope, onSignOut) }
    LaunchedEffect(repository) { state.load() }

    val form = EditorForm(
        isNew = state.selectedId == null,
        name = state.name,
        description = state.description,
        coordinates = state.coordinates,
        coordinatesValid = state.coordinatesValid,
        tags = state.tags.toList(),
        updatedAt = state.updatedAt,
        saving = state.saving,
        message = state.message,
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onSignOut)
            HorizontalDivider()
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                if (maxWidth >= WIDE_THRESHOLD) {
                    TwoPanes(state = state, form = form)
                } else if (state.editing) {
                    EditorPane(
                        form = form,
                        onName = { state.name = it },
                        onDescription = { state.description = it },
                        onCoordinates = { state.coordinates = it },
                        onAddTag = state::addTag,
                        onRemoveTag = state::removeTag,
                        onSave = state::save,
                        onDelete = state::delete,
                        onBack = state::closeEditor,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    CollectionPane(
                        shops = state.shops,
                        selectedId = state.selectedId,
                        loading = state.loading,
                        onNew = state::newShop,
                        onSelect = state::select,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TwoPanes(
    state: AdminConsoleState,
    form: EditorForm
) {
    Row(modifier = Modifier.fillMaxSize()) {
        CollectionPane(
            shops = state.shops,
            selectedId = state.selectedId,
            loading = state.loading,
            onNew = state::newShop,
            onSelect = state::select,
            modifier = Modifier.width(320.dp).fillMaxHeight(),
        )
        VerticalDivider()
        EditorPane(
            form = form,
            onName = { state.name = it },
            onDescription = { state.description = it },
            onCoordinates = { state.coordinates = it },
            onAddTag = state::addTag,
            onRemoveTag = state::removeTag,
            onSave = state::save,
            onDelete = state::delete,
            onBack = null,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
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
    shops: List<CoffeeShop>,
    selectedId: String?,
    loading: Boolean,
    onNew: () -> Unit,
    onSelect: (CoffeeShop) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("My collection · ${shops.size}", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onNew) { Text("+ New") }
        }
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(shops, key = { it.id }) { shop ->
                    ShopRow(
                        shop = shop,
                        selected = shop.id == selectedId,
                        onClick = { onSelect(shop) },
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
    form: EditorForm,
    onName: (String) -> Unit,
    onDescription: (String) -> Unit,
    onCoordinates: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to collection")
                }
            }
            Text(
                if (form.isNew) "New shop" else "Edit shop",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        OutlinedTextField(
            value = form.name,
            onValueChange = onName,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = form.description,
            onValueChange = onDescription,
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = form.coordinates,
            onValueChange = onCoordinates,
            label = { Text("Coordinates") },
            singleLine = true,
            isError = !form.coordinatesValid,
            supportingText = {
                val parsed = parseCoordinates(form.coordinates)
                when {
                    form.coordinates.isBlank() -> Text("Paste \"latitude, longitude\" from Google Maps.")
                    parsed != null -> Text("→ ${parsed.first}, ${parsed.second}")
                    else -> Text("Expected two numbers: latitude, longitude.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        TagEditor(tags = form.tags, onAddTag = onAddTag, onRemoveTag = onRemoveTag)
        if (form.updatedAt != null) {
            Text(
                "Updated ${form.updatedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onSave, enabled = !form.saving) { Text("Save changes") }
            if (!form.isNew) {
                OutlinedButton(onClick = onDelete, enabled = !form.saving) { Text("Delete") }
            }
            form.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun TagEditor(tags: List<String>, onAddTag: (String) -> Unit, onRemoveTag: (String) -> Unit) {
    var custom by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tags", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tags.forEach { tag ->
                InputChip(
                    selected = true,
                    onClick = { onRemoveTag(tag) },
                    label = { Text(tag) },
                    trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove $tag") },
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DEFAULT_TAGS.filter { it !in tags }.forEach { tag ->
                TextButton(onClick = { onAddTag(tag) }) { Text("+ $tag") }
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
                    onAddTag(custom)
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

private val sampleForm = EditorForm(
    isNew = false,
    name = "Coltivare",
    description = "Tiny roastery counter, rotating single-origin filter.",
    coordinates = "35.1739, 33.3618",
    coordinatesValid = true,
    tags = listOf("v60", "roaster"),
    updatedAt = "2026-06-18T14:22:00Z",
    saving = false,
    message = null,
)

@Preview
@Composable
private fun CollectionPanePreview() {
    MaterialTheme {
        Surface {
            CollectionPane(
                shops = sampleShops,
                selectedId = sampleShops.first().id,
                loading = false,
                onNew = {},
                onSelect = {},
            )
        }
    }
}

@Preview
@Composable
private fun EditorPanePreview() {
    MaterialTheme {
        Surface {
            EditorPane(
                form = sampleForm,
                onName = {},
                onDescription = {},
                onCoordinates = {},
                onAddTag = {},
                onRemoveTag = {},
                onSave = {},
                onDelete = {},
                onBack = {},
            )
        }
    }
}

@Preview
@Composable
private fun ShopRowPreview() {
    MaterialTheme {
        Surface {
            ShopRow(shop = sampleShops.first(), selected = true, onClick = {})
        }
    }
}

@Preview
@Composable
private fun TagEditorPreview() {
    MaterialTheme {
        Surface {
            TagEditor(tags = listOf("espresso", "v60"), onAddTag = {}, onRemoveTag = {})
        }
    }
}
