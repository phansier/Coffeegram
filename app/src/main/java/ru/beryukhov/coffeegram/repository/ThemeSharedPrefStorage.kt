package ru.beryukhov.coffeegram.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStateDefault
import ru.beryukhov.coffeegram.store_lib.Storage

internal const val FILENAME = "theme_shared_pref"
internal const val THEME_STATE = "theme_state"
internal const val THEME_CUPERTINO = "theme_dynamic"
internal const val THEME_DYNAMIC = "theme_dynamic"
internal const val THEME_SUMMER = "theme_summer"

class ThemeSharedPrefStorage(private val context: Context) : Storage<ThemeState> {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    }

    override suspend fun getState(): ThemeState? {
        val darkThemeState = sharedPrefs.getString(THEME_STATE, ThemeStateDefault.useDarkTheme.name)
            ?.let { DarkThemeState.valueOf(it) }
        val isCupertino = sharedPrefs.getBooleanOrNull(THEME_CUPERTINO, ThemeStateDefault.isCupertino)
        val isDynamic = sharedPrefs.getBooleanOrNull(THEME_DYNAMIC, ThemeStateDefault.isDynamic)
        val isSummer = sharedPrefs.getBooleanOrNull(THEME_SUMMER, ThemeStateDefault.isSummer)
        return if (darkThemeState != null) {
            ThemeState(
                darkThemeState,
                isCupertino = isCupertino,
                isDynamic = isDynamic,
                isSummer = isSummer
            )
        } else {
            null
        }
    }

    override suspend fun saveState(state: ThemeState) {
        sharedPrefs.edit(commit = false) {
            putString(THEME_STATE, state.useDarkTheme.name)
            putBooleanOrNull(THEME_CUPERTINO, state.isCupertino)
            putBooleanOrNull(THEME_DYNAMIC, state.isDynamic)
            putBooleanOrNull(THEME_SUMMER, state.isSummer)
        }
    }
}

private fun SharedPreferences.Editor.putBooleanOrNull(key: String, value: Boolean?) {
    if (value == null) {
        remove(key)
    } else {
        putBoolean(key, value)
    }
}

private fun SharedPreferences.getBooleanOrNull(key: String, defaultValue: Boolean?): Boolean? =
    if (defaultValue != null && contains(key)) {
        getBoolean(key, defaultValue)
    } else {
        null
    }
