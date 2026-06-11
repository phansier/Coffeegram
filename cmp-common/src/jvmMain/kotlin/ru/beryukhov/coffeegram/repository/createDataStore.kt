package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import java.io.File

private fun dataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        // Absolute path under the user's home so the prefs file is stable regardless of the
        // process working directory (a relative path landed it wherever the app was launched).
        val appDir = File(System.getProperty("user.home"), ".coffeegram").apply { mkdirs() }
        File(appDir, DATA_STORE_FILE_NAME).absolutePath
    }
)

actual fun datastoreModule() = module {
    single<DataStore<Preferences>> { dataStore() }
}
