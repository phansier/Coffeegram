package ru.beryukhov.coffeegram.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.repository.CoffeeShop

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
    var editing by mutableStateOf(false)
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
        editing = true
    }

    fun newShop() {
        selectedId = null
        name = ""
        description = ""
        coordinates = ""
        tags.clear()
        updatedAt = null
        message = null
        editing = true
    }

    fun closeEditor() {
        editing = false
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
                clearForm()
                editing = false
                load()
            }
            saving = false
        }
    }

    private fun clearForm() {
        selectedId = null
        name = ""
        description = ""
        coordinates = ""
        tags.clear()
        updatedAt = null
    }

    private inline fun <T> Result<T>.handle(onOk: (T) -> Unit) {
        onSuccess(onOk)
        onFailure { error ->
            if (error is AdminAuthException) onAuthExpired() else message = error.message
        }
    }
}
