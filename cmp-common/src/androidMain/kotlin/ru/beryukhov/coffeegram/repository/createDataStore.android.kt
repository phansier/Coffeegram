package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import ru.beryukhov.repository.context

internal actual fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath }
)
