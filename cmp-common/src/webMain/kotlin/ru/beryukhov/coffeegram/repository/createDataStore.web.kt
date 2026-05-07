package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

private fun dataStore(): DataStore<Preferences> = createDataStore(
    producePath = { DATA_STORE_FILE_NAME }
)

actual fun datastoreModule() = module {
    single<DataStore<Preferences>> { dataStore() }
}
