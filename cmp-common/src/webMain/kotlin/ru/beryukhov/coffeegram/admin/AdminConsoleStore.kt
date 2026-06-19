package ru.beryukhov.coffeegram.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.repository.CoffeeShop

interface AdminConsoleState {
    val shops: List<CoffeeShop>
    val loading: Boolean
    val saving: Boolean
    val message: String?
    val selectedId: String?
    val editing: Boolean

    val name: String
    val description: String
    val coordinates: String
    val tags: List<String>
    val updatedAt: String?

    val coordinatesValid: Boolean
}

interface AdminConsoleCallbacks {
    fun load()
    fun select(shop: CoffeeShop)
    fun newShop()
    fun closeEditor()
    fun onNameChange(value: String)
    fun onDescriptionChange(value: String)
    fun onCoordinatesChange(value: String)
    fun addTag(raw: String)
    fun removeTag(tag: String)
    fun save()
    fun delete()
}

internal class AdminConsoleStore(
    private val repository: AdminRepository,
    private val scope: CoroutineScope,
    private val onAuthExpired: () -> Unit,
) : AdminConsoleCallbacks, AdminConsoleState {
    override var shops by mutableStateOf<List<CoffeeShop>>(emptyList())
        private set
    override var loading by mutableStateOf(true)
        private set
    override var saving by mutableStateOf(false)
        private set
    override var message by mutableStateOf<String?>(null)
        private set
    override var selectedId by mutableStateOf<String?>(null)
        private set
    override var editing by mutableStateOf(false)
        private set

    override var name by mutableStateOf("")
        private set
    override var description by mutableStateOf("")
        private set
    override var coordinates by mutableStateOf("")
        private set
    override val tags = mutableStateListOf<String>()
    override var updatedAt by mutableStateOf<String?>(null)
        private set

    override val coordinatesValid: Boolean
        get() = coordinates.isBlank() || parseCoordinates(coordinates) != null

    override fun load() {
        scope.launch {
            repository.list().handle { shops = it }
            loading = false
        }
    }

    override fun select(shop: CoffeeShop) {
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

    override fun newShop() {
        clearForm()
        message = null
        editing = true
    }

    override fun closeEditor() {
        editing = false
    }

    override fun onNameChange(value: String) {
        name = value
    }

    override fun onDescriptionChange(value: String) {
        description = value
    }

    override fun onCoordinatesChange(value: String) {
        coordinates = value
    }

    override fun addTag(raw: String) {
        val tag = raw.trim().lowercase()
        if (tag.isNotEmpty() && tag !in tags) tags.add(tag)
    }

    override fun removeTag(tag: String) {
        tags.remove(tag)
    }

    override fun save() {
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

    override fun delete() {
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
