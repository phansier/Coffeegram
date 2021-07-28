package ru.beryukhov.coffeegram.repository

import android.content.Context

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.store_lib.Storage

private object PreferencesKeys {
    val THEME_STATE_KEY = stringPreferencesKey(THEME_STATE)
}

class ThemeDataStorePrefStorage(private val context: Context): Storage<ThemeState> {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = FILENAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(
                    context, FILENAME
                    /*, keysToMigrate = MIGRATE_ALL_KEYS*/
                )
            )
        }
    )



    override suspend fun getState(): ThemeState? {
        //do not confuse with `lastOrNull()`, it will be waiting for completion inside otherwise
        return context.dataStore.data.firstOrNull()
            ?.get(PreferencesKeys.THEME_STATE_KEY)
            ?.let { ThemeState.valueOf(it) }
    }

    override suspend fun saveState(state: ThemeState) {
        context.dataStore.edit {
            preferences -> preferences[PreferencesKeys.THEME_STATE_KEY] = state.name
        }
        /*context.dataStore.updateData{ preferences ->
            preferences.toMutablePreferences()
                .also { it[PreferencesKeys.THEME_STATE_KEY] = state.name }
        }*/
    }

}