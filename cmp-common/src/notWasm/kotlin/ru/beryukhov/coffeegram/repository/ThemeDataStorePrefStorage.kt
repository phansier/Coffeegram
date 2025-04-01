package ru.beryukhov.coffeegram.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.store_lib.Storage

internal const val DATA_STORE_FILE_NAME = "theme.preferences_pb"

internal const val THEME_STATE = "theme_state"
internal const val THEME_DYNAMIC = "theme_dynamic"
internal const val THEME_SUMMER = "theme_summer"
internal const val THEME_CUPERTINO = "theme_cupertino"

private object PreferencesKeys {
    val THEME_STATE_KEY = stringPreferencesKey(THEME_STATE)
    val THEME_DYNAMIC_KEY = booleanPreferencesKey(THEME_DYNAMIC)
    val THEME_SUMMER_KEY = booleanPreferencesKey(THEME_SUMMER)
    val THEME_CUPERTINO_KEY = booleanPreferencesKey(THEME_CUPERTINO)
}

class ThemeDataStorePrefStorage(private val dataStore: DataStore<Preferences>) : Storage<ThemeState> {

    override suspend fun getState(): ThemeState? {
        // do not confuse with `lastOrNull()`, it will be waiting for completion inside otherwise
        val prefs = dataStore.data.firstOrNull()
        val darkThemeState = prefs?.get(PreferencesKeys.THEME_STATE_KEY)
            ?.let { DarkThemeState.valueOf(it) }
        val isCupertino = prefs?.get(PreferencesKeys.THEME_CUPERTINO_KEY)
        val isDynamic = prefs?.get(PreferencesKeys.THEME_DYNAMIC_KEY)
        val isSummer = prefs?.get(PreferencesKeys.THEME_SUMMER_KEY)
        return if (darkThemeState != null) {
            ThemeState(
                useDarkTheme = darkThemeState,
                isCupertino = isCupertino,
                isDynamic = isDynamic,
                isSummer = isSummer,
            )
        } else {
            null
        }
    }

    override suspend fun saveState(state: ThemeState) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_STATE_KEY] = state.useDarkTheme.name
            state.isCupertino?.let { preferences[PreferencesKeys.THEME_CUPERTINO_KEY] = it }
            state.isDynamic?.let { preferences[PreferencesKeys.THEME_DYNAMIC_KEY] = it }
            state.isSummer?.let { preferences[PreferencesKeys.THEME_SUMMER_KEY] = it }
        }
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        produceFile = { producePath().toPath() },
    )
}

internal expect fun datastoreModule(): Module
