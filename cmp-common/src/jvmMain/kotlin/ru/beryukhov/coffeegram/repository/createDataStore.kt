package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal actual fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = { DATA_STORE_FILE_NAME }
)
