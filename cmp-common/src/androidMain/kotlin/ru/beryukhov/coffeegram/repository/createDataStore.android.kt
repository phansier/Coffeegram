package ru.beryukhov.coffeegram.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

private fun dataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath }
 )

actual fun datastoreModule() = module {
    single<DataStore<Preferences>> { dataStore(get()) }
}
